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
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.dao.PersonAliasDao;
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
public class ContestGroupMigrateController extends AbstractMigrateController  {

    private final SecurityService securityService;
    private final ContestGroupService contestGroupService;
    private final ContestTagService contestTagService;

    @GetMapping("/migrate/groups")
    @IsBbrAdmin
    public String home(Model model)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning groups repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/groups/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/groups/clone")
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
        model.addAttribute("Next", "/migrate/groups/0");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/groups/{index}")
    @IsBbrAdmin
    public String clone(Model model, @PathVariable("index") int index) throws GitAPIException, IOException, JDOMException {

        List<String> messages = new ArrayList<>();
        String[] directories = this.fetchDirectories();
        try {
            String indexLetter = directories[index];
            this.importContestGroups(indexLetter);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            return "redirect:/";
        }
        
        int nextIndex = index + 1;
        
        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/groups/" + nextIndex);

        return "migrate/migrate";
    }

    private void importContestGroups(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/Groups/" + indexLetter);
        if (letterLevel.exists()) {
            String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

            for (String eachFile : files) {
                File eachBandFile = new File(BASE_PATH + "/Groups/" + indexLetter + "/" + eachFile);
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
        }
    }

    private void createPreviousName(ContestGroupDao group, Element oldNameElement) {
        String name = oldNameElement.getChildText("name");
        // does it already exist?

        Optional<PersonAliasDao> existingAlias = this.contestGroupService.aliasExists(group, name);
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
