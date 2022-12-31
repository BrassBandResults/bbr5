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
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAlias;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PiecesMigrateController extends AbstractMigrateController  {

    private final PieceService pieceService;
    private final SecurityService securityService;
    private final PersonService personService;

    @GetMapping("/migrate/pieces")
    // TODO @IsBbrAdmin
    public String home(Model model)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning pieces repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/pieces/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/pieces/clone")
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
        model.addAttribute("Next", "/migrate/pieces/0");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/pieces/{index}")
    // TODO @IsBbrAdmin
    public String clone(Model model, @PathVariable("index") int index) throws GitAPIException, IOException, JDOMException {

        List<String> messages = new ArrayList<>();
        String[] directories = this.fetchDirectories();
        try {
            String indexLetter = directories[index];
            this.importPieces(indexLetter);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            return "redirect:/";
        }
        
        int nextIndex = index + 1;
        
        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/pieces/" + nextIndex);

        return "migrate/migrate";
    }

    private void importPieces(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/Pieces/" + indexLetter);
        String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

        for (String eachFile : files) {
            File eachBandFile = new File(BASE_PATH + "/Pieces/" + indexLetter + "/" + eachFile);
            String filename = eachBandFile.getAbsolutePath();

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

            PieceDao newPiece = new PieceDao();
            newPiece.setOldId(rootNode.getAttributeValue("id"));
            newPiece.setSlug(rootNode.getChildText("slug"));
            newPiece.setName(rootNode.getChildText("name"));
            newPiece.setNotes(this.notBlank(rootNode, "notes"));
            newPiece.setYear(this.notBlank(rootNode, "year"));
            if (newPiece.getYear() != null && newPiece.getYear().length() > 4) {
                System.out.println("****" + newPiece.getYear());
                newPiece.setYear(newPiece.getYear().substring(0,4));
            }

            PersonDao composer = null;
            if (rootNode.getChildText("composer") != null) {
                composer = this.personService.fetchBySlug(rootNode.getChild("composer").getAttributeValue("slug"));
            }

            PersonDao arranger = null;
            if (rootNode.getChildText("arranger") != null) {
                arranger = this.personService.fetchBySlug(rootNode.getChild("arranger").getAttributeValue("slug"));
            }

            newPiece.setComposer(composer);
            newPiece.setArranger(arranger);

            newPiece.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
            newPiece.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

            newPiece.setCreated(this.notBlankDateTime(rootNode, "created"));
            newPiece.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

            // notes
            newPiece = this.pieceService.create(newPiece);

            Element previousNames = rootNode.getChild("previous_names");
            List<Element> previousNameNodes = previousNames.getChildren();
            for (Element eachOldName : previousNameNodes) {
                this.createPreviousName(newPiece, eachOldName);
            }

            System.out.println(newPiece.getName());
        }
    }

    private void createPreviousName(PieceDao piece, Element oldNameElement) {
        PieceAlias previousName = new PieceAlias();
        previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
        previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
        previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
        previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
        previousName.setName(oldNameElement.getChildText("name"));
        previousName.setHidden(false);

        this.pieceService.createAlternativeName(piece, previousName);
    }
}
