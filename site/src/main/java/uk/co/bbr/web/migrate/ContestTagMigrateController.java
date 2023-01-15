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
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAlias;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContestTagMigrateController extends AbstractMigrateController  {

    private final SecurityService securityService;
    private final ContestTagService contestTagService;

    @GetMapping("/migrate/tags")
    @IsBbrAdmin
    public String home(Model model)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning tags repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/tags/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/tags/clone")
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
        model.addAttribute("Next", "/migrate/tags/0");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/tags/{index}")
    @IsBbrAdmin
    public String clone(Model model, @PathVariable("index") int index) throws GitAPIException, IOException, JDOMException {

        List<String> messages = new ArrayList<>();
        String[] directories = this.fetchDirectories();
        try {
            String indexLetter = directories[index];
            this.importContestTags(indexLetter);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            return "redirect:/";
        }
        
        int nextIndex = index + 1;
        
        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/tags/" + nextIndex);

        return "migrate/migrate";
    }

    private void importContestTags(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/Tags/" + indexLetter);
        if (letterLevel.exists()) {
            String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

            for (String eachFile : files) {
                File eachBandFile = new File(BASE_PATH + "/Tags/" + indexLetter + "/" + eachFile);
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

                ContestTagDao contestTag = new ContestTagDao();
                contestTag.setOldId(rootNode.getAttributeValue("id"));
                contestTag.setSlug(rootNode.getChildText("slug"));
                contestTag.setName(rootNode.getChildText("name"));

                contestTag.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
                contestTag.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

                contestTag.setCreated(this.notBlankDateTime(rootNode, "created"));
                contestTag.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

                // notes
                contestTag = this.contestTagService.migrate(contestTag);

                System.out.println(contestTag.getName());
            }
        }
    }
}
