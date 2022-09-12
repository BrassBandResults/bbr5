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

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BandsMigrateController {

    private static final String BASE_PATH = "/tmp/bbr";

    @GetMapping("/migrate/bands")
    // TODO @IsBbrAdmin
    public String home(Model model)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning bands repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/bands/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/bands/clone")
    // TODO @IsBbrAdmin
    public String clone(Model model) throws GitAPIException {
        Git.cloneRepository()
                .setURI("https://github.com/BrassBandResults/bbr-data.git")
                .setDirectory(new File(BASE_PATH))
                .call();

        List<String> messages = new ArrayList<>();
        messages.add("Repository clone complete...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/bands/0");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/bands/{index}")
    // TODO @IsBbrAdmin
    public String clone(Model model, @PathVariable("index") int index) throws GitAPIException, IOException, JDOMException {

        List<String> messages = new ArrayList<>();
        String[] directories = this.fetchDirectories();
        try {
            String indexLetter = directories[index];
            this.importBands(indexLetter);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            return "redirect:/";
        }
        
        int nextIndex = index + 1;
        
        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/bands/" + nextIndex);

        return "migrate/migrate";
    }

    private void importBands(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/Bands/" + indexLetter);
        String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

        for (String eachFile : files) {
            File eachBandFile = new File(BASE_PATH + "/Bands/" + indexLetter + "/" + eachFile);
            String filename = eachBandFile.getAbsolutePath();

            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(new File(filename));
            Element rootNode = doc.getRootElement();
            String oldBandId = rootNode.getAttributeValue("id");
            String bandName = rootNode.getChildText("band");
            String bandSlug = rootNode.getChildText("slug");

            System.out.println(oldBandId + " " + bandName + " " + bandSlug);
        }
    }

    private String[] fetchDirectories() {
        File topLevel = new File(BASE_PATH + "/Bands");
        return Arrays.stream(topLevel.list((current, name) -> new File(current, name).isDirectory())).sorted().toArray(String[]::new);
    }
}
