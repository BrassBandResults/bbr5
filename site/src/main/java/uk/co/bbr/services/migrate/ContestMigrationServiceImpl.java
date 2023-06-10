package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@IgnoreCoverage
public class ContestMigrationServiceImpl extends AbstractMigrationServiceImpl implements ContestMigrationService, SlugTools {

    private final SecurityService securityService;
    private final UserService userService;
    private final ContestGroupService contestGroupService;
    private final ContestTagService contestTagService;
    private final ContestService contestService;
    private final RegionService regionService;
    private final SectionService sectionService;
    private final ContestTypeService contestTypeService;

    @Override
    public void migrate(Element rootNode) {
        ContestDao contest = new ContestDao();
        contest.setOldId(rootNode.getAttributeValue("id"));
        contest.setSlug(rootNode.getChildText("slug"));
        contest.setName(rootNode.getChildText("name"));
        contest.setNotes(rootNode.getChildText("notes"));

        Element groupNode = rootNode.getChild("group");
        if (groupNode != null) {
            String groupSlug = groupNode.getAttributeValue("slug");
            Optional<ContestGroupDao> group = this.contestGroupService.fetchBySlug(groupSlug);
            if (group.isEmpty()) {
                throw NotFoundException.groupNotFoundBySlug(groupSlug);
            }
            contest.setContestGroup(group.get());
        }

        contest.setDescription(this.notBlank(rootNode, "description"));
        if (contest.getDescription() != null && contest.getDescription().equals(contest.getName())) {
            contest.setDescription(null);
        }

        Element regionNode = rootNode.getChild("region");
        if (regionNode != null) {
            String regionSlug = regionNode.getAttributeValue("slug");
            Optional<RegionDao> region = this.regionService.fetchBySlug(regionSlug);
            if (region.isEmpty()) {
                throw new NotFoundException("Region not found");
            }
            contest.setRegion(region.get());
        }

        Element sectionNode = rootNode.getChild("section");
        if (sectionNode != null) {
            String sectionSlug = sectionNode.getAttributeValue("slug");
            Optional<SectionDao> section = this.sectionService.fetchBySlug(sectionSlug);
            if (section.isEmpty()) {
                throw new NotFoundException("Section not found");
            }
            contest.setSection(section.get());
        }

        Element contestTypeNode = rootNode.getChild("contest_type_link");
        if (contestTypeNode != null) {
            String contestTypeName = contestTypeNode.getText().replace("Chuch", "Church");
            Optional<ContestTypeDao> contestType = this.contestTypeService.fetchByName(contestTypeName);
            if (contestType.isEmpty()) {
                throw new NotFoundException("ContestType with name " + contestTypeNode.getText() + " not found");
            }
            contest.setDefaultContestType(contestType.get());
        }

        contest.setOrdering(this.notBlankInteger(rootNode, "ordering"));
        contest.setRepeatPeriod(this.notBlankInteger(rootNode, "period"));

        contest.setExtinct(this.notBlankBoolean(rootNode, "extinct"));
        contest.setExcludeFromGroupResults(this.notBlankBoolean(rootNode, "exclude_from_group_results"));
        contest.setAllEventsAdded(this.notBlankBoolean(rootNode, "all_events_added"));
        contest.setPreventFutureBands(this.notBlankBoolean(rootNode, "prevent_future_bands"));

        // tags
        Element tags = rootNode.getChild("tags");
        List<Element> tagNodes = tags.getChildren();
        for (Element eachTag : tagNodes) {
            this.createTag(contest, eachTag);
        }

        contest.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService, this.userService));
        contest.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService, this.userService));

        contest.setCreated(this.notBlankDateTime(rootNode, "created"));
        contest.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

        contest = this.contestService.migrate(contest);

        // aliases
        Element previousNames = rootNode.getChild("previous_names");
        List<Element> previousNameNodes = previousNames.getChildren();
        for (Element eachOldName : previousNameNodes) {
            this.createPreviousName(contest, eachOldName);
        }

        System.out.println(contest.getName());
    }

    private void createPreviousName(ContestDao contest, Element oldNameElement) {
        String name = oldNameElement.getChildText("name");
        // does it already exist?

        Optional<ContestAliasDao> existingAlias = this.contestService.aliasExists(contest, name);
        if (existingAlias.isEmpty()) {

            ContestAliasDao previousName = new ContestAliasDao();
            previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService, this.userService));
            previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService, this.userService));
            previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
            previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
            previousName.setName(name);
            previousName.setOldId(oldNameElement.getAttributeValue("id"));

            this.contestService.migrateAlias(contest, previousName);
        }
    }

    private void createTag(ContestDao contest, Element oldNameElement) {
        String name = oldNameElement.getText();

        Optional<ContestTagDao> contestTag = this.contestTagService.fetchByName(name);
        if (contestTag.isEmpty()) {
            throw new NotFoundException("Contest tag with name " + name + " not found");
        }

        contest.getTags().add(contestTag.get());
    }
}
