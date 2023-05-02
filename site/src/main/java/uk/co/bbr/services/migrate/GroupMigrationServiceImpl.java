package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@IgnoreCoverage
public class GroupMigrationServiceImpl extends AbstractMigrationServiceImpl implements GroupMigrationService, SlugTools {
    private final SecurityService securityService;
    private final ContestGroupService contestGroupService;
    private final ContestTagService contestTagService;

    @Override
    public void migrate(Element rootNode) {
        ContestGroupDao contestGroup = new ContestGroupDao();
        contestGroup.setOldId(rootNode.getAttributeValue("id"));
        contestGroup.setSlug(rootNode.getChildText("slug"));
        contestGroup.setName(rootNode.getChildText("name"));
        contestGroup.setNotes(rootNode.getChildText("notes"));
        contestGroup.setGroupType(ContestGroupType.NORMAL);
        if (contestGroup.getName().contains("Whit Friday")) {
            contestGroup.setGroupType(ContestGroupType.WHIT_FRIDAY);
        }

        // tags
        Element tags = rootNode.getChild("tags");
        List<Element> tagNodes = tags.getChildren();
        for (Element eachTag : tagNodes) {
            this.createTag(contestGroup, eachTag);
        }

        contestGroup.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
        contestGroup.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

        contestGroup.setCreated(this.notBlankDateTime(rootNode, "created"));
        contestGroup.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

        contestGroup = this.contestGroupService.migrate(contestGroup);

        // aliases
        Element previousNames = rootNode.getChild("previous_names");
        List<Element> previousNameNodes = previousNames.getChildren();
        for (Element eachOldName : previousNameNodes) {
            this.createPreviousName(contestGroup, eachOldName);
        }

        System.out.println(contestGroup.getName());
    }

    private void createPreviousName(ContestGroupDao group, Element oldNameElement) {
        String name = oldNameElement.getChildText("name");
        // does it already exist?

        Optional<ContestGroupAliasDao> existingAlias = this.contestGroupService.aliasExists(group, name);
        if (existingAlias.isEmpty()) {

            ContestGroupAliasDao previousName = new ContestGroupAliasDao();
            previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
            previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
            previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
            previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
            previousName.setName(name);

            this.contestGroupService.migrateAlias(group, previousName);
        }
    }

    private void createTag(ContestGroupDao group, Element oldNameElement) {
        String name = oldNameElement.getText();

        Optional<ContestTagDao> contestTag = this.contestTagService.fetchByName(name);
        if (contestTag.isEmpty()) {
            throw new NotFoundException("Contest tag with name " + name + " not found");
        }

        group.getTags().add(contestTag.get());
    }
}
