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
import uk.co.bbr.services.people.PeopleService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.SecurityService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PeopleMigrateController extends AbstractMigrateController  {

    private final PeopleService peopleService;
    private final SecurityService securityService;

    @GetMapping("/migrate/people")
    // TODO @IsBbrAdmin
    public String home(Model model)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning people repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/people/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/people/clone")
    // TODO @IsBbrAdmin
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
        model.addAttribute("Next", "/migrate/people/0");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/people/{index}")
    // TODO @IsBbrAdmin
    public String clone(Model model, @PathVariable("index") int index) throws GitAPIException, IOException, JDOMException {

        List<String> messages = new ArrayList<>();
        String[] directories = this.fetchDirectories();
        try {
            String indexLetter = directories[index];
            this.importPeople(indexLetter);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            return "redirect:/";
        }
        
        int nextIndex = index + 1;
        
        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/people/" + nextIndex);

        return "migrate/migrate";
    }

    private void importPeople(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/People/" + indexLetter);
        String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

        for (String eachFile : files) {
            File eachBandFile = new File(BASE_PATH + "/People/" + indexLetter + "/" + eachFile);
            String filename = eachBandFile.getAbsolutePath();
            System.out.println(filename);

            Document doc = null;
            try {
                SAXBuilder sax = new SAXBuilder();
                doc = sax.build(new File(filename));
            }
            catch (Throwable ex) {
                ex.printStackTrace();
                continue;
            }
            Element rootNode = doc.getRootElement();

            PersonDao newPerson = new PersonDao();
            newPerson.setOldId(rootNode.getAttributeValue("id"));
            newPerson.setSlug(rootNode.getChildText("slug"));
            newPerson.setFirstNames(rootNode.getChildText("first_names"));
            newPerson.setSurname(rootNode.getChildText("surname"));
            newPerson.setKnownFor(this.notBlank(rootNode, "bandname"));
            newPerson.setNotes(this.notBlank(rootNode, "notes"));
            newPerson.setDeceased("True".equals(rootNode.getChildText("deceased")));
            newPerson.setStartDate(this.notBlankDate(rootNode, "start"));
            newPerson.setEndDate(this.notBlankDate(rootNode, "end"));

            newPerson.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
            newPerson.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

            newPerson.setCreated(this.notBlankDateTime(rootNode, "created"));
            newPerson.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

            // notes
            newPerson = this.peopleService.create(newPerson);

            Element previousNames = rootNode.getChild("previous_names");
            List<Element> previousNameNodes = previousNames.getChildren();
            for (Element eachOldName : previousNameNodes) {
                this.createPreviousName(newPerson, eachOldName);
            }

            System.out.println(newPerson.getSlug());
        }
    }

    private void createPreviousName(PersonDao person, Element oldNameElement) {
        PersonAliasDao previousName = new PersonAliasDao();
        previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
        previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
        previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
        previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
        previousName.setOldName(oldNameElement.getChildText("name"));
        previousName.setHidden(this.notBlankBoolean(oldNameElement, "hidden"));

        this.peopleService.createAlternativeName(person, previousName);
    }
}
