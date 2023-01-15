package uk.co.bbr.web.migrate;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestsMigrateController extends AbstractMigrateController  {

    private final SecurityService securityService;
    private final ContestGroupService contestGroupService;
    private final ContestTagService contestTagService;
    private final ContestService contestService;
    private final RegionService regionService;
    private final SectionService sectionService;
    private final ContestTypeService contestTypeService;

    @GetMapping("/migrate/contests")
    @IsBbrAdmin
    public String home(Model model)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning contests repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/contests/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/contests/clone")
    @IsBbrAdmin
    public String clone(Model model) throws GitAPIException {
        if (!new File(BASE_PATH).exists()) {

            Git.cloneRepository()
                    .setURI("https://github.com/BrassBandResults/bbr-data.git")
                    .setDirectory(new File(BASE_PATH))
                    .call();
        }

        List<String> messages = new ArrayList<>();
        messages.add("Repository clone complete...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/contests/0");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/contests/{index}")
    @IsBbrAdmin
    public String clone(Model model, @PathVariable("index") int index) throws GitAPIException, IOException, JDOMException {

        List<String> messages = new ArrayList<>();
        String[] directories = this.fetchDirectories();
        try {
            String indexLetter = directories[index];
            this.importContestContests(indexLetter);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            return "redirect:/";
        }
        
        int nextIndex = index + 1;
        
        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/contests/" + nextIndex);

        return "migrate/migrate";
    }

    private void importContestContests(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/Contests/" + indexLetter);
        if (letterLevel.exists()) {
            String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

            for (String eachFile : files) {
                File eachBandFile = new File(BASE_PATH + "/Contests/" + indexLetter + "/" + eachFile);
                String filename = eachBandFile.getAbsolutePath();

                Document doc = null;
                try {
                    SAXBuilder sax = new SAXBuilder();
                    doc = sax.build(new File(filename));
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    continue;
                }
                Element rootNode = doc.getRootElement();

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
                        throw new NotFoundException("Group with slug " + groupSlug + " not found");
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

                contest.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
                contest.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

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
        }
    }

    private void createPreviousName(ContestDao contest, Element oldNameElement) {
        String name = oldNameElement.getChildText("name");
        // does it already exist?

        Optional<ContestAliasDao> existingAlias = this.contestService.aliasExists(contest, name);
        if (existingAlias.isEmpty()) {

            ContestAliasDao previousName = new ContestAliasDao();
            previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
            previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
            previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
            previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
            previousName.setName(name);

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
