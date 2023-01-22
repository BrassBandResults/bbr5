package uk.co.bbr.web.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.migrate.EventMigrationService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static uk.co.bbr.web.migrate.MigrateController.BASE_PATH;

@Controller
@RequiredArgsConstructor
public class MigrateResultsController {

    private final EventMigrationService eventMigrationService;

    @GetMapping("/migrate/Results/all/{index}/{yearIndex}")
    @IsBbrAdmin
    public String processResults(Model model, @PathVariable("index") int index, @PathVariable("yearIndex") int yearIndex) {
        if (index > 6) {
            return "redirect:/";
        }

        int nextIndex = index;
        int nextYearIndex = yearIndex;

        List<String> messages = new ArrayList<>();

        File topLevel = new File(BASE_PATH + "/Results/");
        String[] directories = Arrays.stream(Objects.requireNonNull(topLevel.list((current, name) -> new File(current, name).isDirectory()))).sorted().toArray(String[]::new);
        try {
            String folder = directories[index];

            messages.add("Processing folder " + folder + "...");
            File indexFolder = new File(BASE_PATH + "/Results/" + folder);

            String[] yearFolders = Arrays.stream(Objects.requireNonNull(indexFolder.list((current, name) -> new File(current, name).isDirectory()))).sorted().toArray(String[]::new);
            File yearFolder = new File(BASE_PATH + "/Results/" + folder + "/" + yearFolders[yearIndex]);
            messages.add("Processing year " + yearFolders[yearIndex] + "...");
            this.importYear(yearFolder);
            nextYearIndex = yearIndex + 1;
        }
        catch (IndexOutOfBoundsException ex) {
            nextIndex = index + 1;
            nextYearIndex = 0;
        }

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/Results/all/" + nextIndex + "/" + nextYearIndex);

        return "migrate/migrate";
    }

    private void importYear(File yearFolder) {
        Collection<File> allFilesForYear = this.listFileTree(yearFolder);
        for (File eachFile : allFilesForYear) {
            String filename = eachFile.getAbsolutePath();

            System.out.println(filename);

            Document doc;
            try {
                SAXBuilder sax = new SAXBuilder();
                doc = sax.build(new File(filename));
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
            Element rootNode = doc.getRootElement();
            this.eventMigrationService.migrate(rootNode);
        }
    }

    public static Collection<File> listFileTree(File dir) {
        Set<File> fileTree = new HashSet<File>();
        if(dir==null||dir.listFiles()==null){
            return fileTree;
        }
        for (File entry : dir.listFiles()) {
            if (entry.isFile()) fileTree.add(entry);
            else fileTree.addAll(listFileTree(entry));
        }
        return fileTree;
    }


}
