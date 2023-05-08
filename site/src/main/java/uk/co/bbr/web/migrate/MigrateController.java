package uk.co.bbr.web.migrate;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.migrate.BandMigrationService;
import uk.co.bbr.services.migrate.ContestMigrationService;
import uk.co.bbr.services.migrate.GroupMigrationService;
import uk.co.bbr.services.migrate.PersonMigrationService;
import uk.co.bbr.services.migrate.PieceMigrationService;
import uk.co.bbr.services.migrate.TagsMigrationService;
import uk.co.bbr.services.migrate.VenueMigrationService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@IgnoreCoverage
public class MigrateController {
    protected static final String BASE_PATH = "/tmp/bbr";

    private final BandMigrationService bandMigrationService;
    private final GroupMigrationService groupMigrationService;
    private final ContestMigrationService contestMigrationService;
    private final TagsMigrationService tagsMigrationService;
    private final PersonMigrationService personMigrationService;
    private final PieceMigrationService pieceMigrationService;
    private final VenueMigrationService venueMigrationService;

    @GetMapping("/migrate/{type}")
    @IsBbrAdmin
    public String home(Model model, @PathVariable(name="type") String type)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/" + type + "/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/{type}/clone")
    @IsBbrAdmin
    public String clone(Model model, @PathVariable(name="type") String type) throws GitAPIException {
        if (!new File(BASE_PATH).exists()) {

            Git.cloneRepository()
                    .setURI("https://github.com/BrassBandResults/bbr-data.git")
                    .setDirectory(new File(BASE_PATH))
                    .call();
        }

        List<String> messages = new ArrayList<>();
        messages.add("Repository clone complete...");

        String next = "/migrate/" + type + "/1/0";

        if (type.startsWith("Results")) {
            next = "/migrate/Results/all/0/0";
        }

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", next);

        return "migrate/migrate";
    }

    @GetMapping("/migrate/{type}/{pass}/{index}")
    @IsBbrAdmin
    public String process(Model model, @PathVariable("index") int index, @PathVariable("type") String type, @PathVariable("pass") int pass) {

        boolean twoPasses = pass == 1 && (type.equals("Venues") || type.equals("Bands"));

        List<String> messages = new ArrayList<>();

        File topLevel = new File(BASE_PATH + "/" + type);
        String[] directories = Arrays.stream(Objects.requireNonNull(topLevel.list((current, name) -> new File(current, name).isDirectory()))).sorted().toArray(String[]::new);
        try {
            String indexLetter = directories[index];
            this.importType(type, indexLetter, pass);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            if (twoPasses) {
                return "redirect:/migrate/" + type + "/2/0";
            }
            return "redirect:/";
        }

        int nextIndex = index + 1;

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/" + type + "/" + pass + "/" + nextIndex);

        return "migrate/migrate";
    }

    private void importType(String type, String indexLetter, int pass) {
        File letterLevel = new File(BASE_PATH + "/"+ type +"/" + indexLetter);
        System.out.println("Looking at " + letterLevel);
        if (letterLevel.exists()) {
            String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

            for (String eachFile : files) {
                File eachBandFile = new File(BASE_PATH + "/" + type + "/" + indexLetter + "/" + eachFile);
                String filename = eachBandFile.getAbsolutePath();

                Document doc;
                try {
                    SAXBuilder sax = new SAXBuilder();
                    doc = sax.build(new File(filename));
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
                Element rootNode = doc.getRootElement();

                switch(type) {
                    case "Bands":
                        this.bandMigrationService.migrate(rootNode);
                        break;
                    case "Groups":
                        this.groupMigrationService.migrate(rootNode);
                        break;
                    case "Contests":
                        this.contestMigrationService.migrate(rootNode);
                        break;
                    case "Tags":
                        this.tagsMigrationService.migrate(rootNode);
                        break;
                    case "People":
                        this.personMigrationService.migrate(rootNode);
                        break;
                    case "Pieces":
                        this.pieceMigrationService.migrate(rootNode);
                        break;
                    case "Venues":
                        this.venueMigrationService.migrate(rootNode, pass);
                        break;
                    default:
                        throw new UnsupportedOperationException("No support for " + type);
                }
            }
        }
    }

    protected final String[] fetchDirectories(String type) {
        File topLevel = new File(BASE_PATH + "/" + type);
        return Arrays.stream(Objects.requireNonNull(topLevel.list((current, name) -> new File(current, name).isDirectory()))).sorted().toArray(String[]::new);
    }
}
