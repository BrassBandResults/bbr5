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
import org.xml.sax.SAXParseException;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.dao.BandPreviousNameDao;
import uk.co.bbr.services.band.dao.BandRelationshipDao;
import uk.co.bbr.services.band.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.band.types.BandStatus;
import uk.co.bbr.services.band.types.RehearsalDay;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.section.SectionService;
import uk.co.bbr.services.section.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BandsMigrateController {

    private static final String BASE_PATH = "/tmp/bbr";

    private final RegionService regionService;
    private final BandService bandService;
    private final SectionService sectionService;
    private final SecurityService securityService;

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
        if (!new File(BASE_PATH).exists()) {

            Git.cloneRepository()
                    .setURI("https://github.com/BrassBandResults/bbr-data.git")
                    .setDirectory(new File(BASE_PATH))
                    .call();
        }

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

    private String notBlank(Element node, String childName) {
        if (node == null) {
            throw new UnsupportedOperationException("Node passed is null");
        }
        String value = node.getChildText(childName);
        if ("None".equals(value)) {
            return null;
        }
        if (value == null) {
            return null;
        }

        if (value.trim().length() == 0) {
            return null;
        }

        return value;
    }

    private LocalDate notBlankDate(Element node, String childName) {
        String value = this.notBlank(node, childName);
        if (value == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(value, formatter);
    }

    private LocalDateTime notBlankDateTime(Element node, String childName) {
        String value = this.notBlank(node, childName);
        if (value == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(value, formatter);
    }

    private boolean notBlankBoolean(Element node, String childName) {
        String value = this.notBlank(node, childName);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        return false;
    }

    private long createUser(String username) {
        if (username == null) {
            return 1;
        }

        Optional<BbrUserDao> user = this.securityService.fetchUserByUsercode(username);
        if (user.isPresent()) {
            return user.get().getId();
        }

        BbrUserDao newUser = this.securityService.createUser(username, "NoPassword", "migrated@brassbandresults.co.uk");
        return newUser.getId();
    }

    private void importBands(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/Bands/" + indexLetter);
        String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

        for (String eachFile : files) {
            File eachBandFile = new File(BASE_PATH + "/Bands/" + indexLetter + "/" + eachFile);
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

            BandDao newBand = new BandDao();
            newBand.setOldId(rootNode.getAttributeValue("id"));
            newBand.setName(rootNode.getChildText("name"));
            newBand.setSlug(rootNode.getChildText("slug"));
            if (!rootNode.getChildText("website").equals("http://")){
                newBand.setWebsite(this.notBlank(rootNode, "website"));
            }

            newBand.setTwitterName(this.notBlank(rootNode, "twitter"));

            RegionDao region = this.regionService.findBySlug(rootNode.getChild("region").getAttributeValue("slug"));
            newBand.setRegion(region);

            newBand.setLatitude(this.notBlank(rootNode, "latitude"));
            newBand.setLongitude(this.notBlank(rootNode, "longitude"));

            newBand.setStartDate(this.notBlankDate(rootNode, "start"));
            newBand.setEndDate(this.notBlankDate(rootNode, "end"));

            String statusText = this.notBlank(rootNode, "status");
            if (statusText != null && statusText.length() > 0) {
                newBand.setStatus(BandStatus.fromDescription(statusText));
            }
            if ("True".equalsIgnoreCase(this.notBlank(rootNode, "scratch_band"))){
                newBand.setStatus(BandStatus.SCRATCH);
            }

            String gradingName = this.notBlank(rootNode, "grading");
            if (gradingName != null) {
                SectionDao section = this.sectionService.fetchByName(gradingName);
                newBand.setSection(section);
            }

            newBand.setMapperId(this.createUser(this.notBlank(rootNode, "mapper")));
            newBand.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner")));
            newBand.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy")));

            newBand.setCreated(this.notBlankDateTime(rootNode, "created"));
            newBand.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

            newBand.setNotes(this.notBlank(rootNode, "notes"));

            // notes
            newBand = this.bandService.create(newBand);

            this.createBandRehearsalNight(newBand, this.notBlank(rootNode,"rehearsal1"));
            this.createBandRehearsalNight(newBand, this.notBlank(rootNode,"rehearsal2"));

            Element previousNames = rootNode.getChild("previous_names");
            List<Element> previousNameNodes = previousNames.getChildren();
            for (Element eachOldName : previousNameNodes) {
                this.createPreviousName(newBand, eachOldName);
            }

            System.out.println(newBand.getName());
        }
    }

    private void linkBands(String indexLetter) {
        // go through list again and do bank links
        File letterLevel = new File(BASE_PATH + "/Bands/" + indexLetter);
        String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

        for (String eachFile : files) {
            File eachBandFile = new File(BASE_PATH + "/Bands/" + indexLetter + "/" + eachFile);
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

            Long bandOldId = Long.parseLong(rootNode.getAttributeValue("id"));

            Element parent1Element = rootNode.getChild("parent1");
            Element parent2Element = rootNode.getChild("parent2");

            this.createBandLink(bandOldId, parent1Element);
            this.createBandLink(bandOldId, parent2Element);
        }
    }

    private void createBandLink(Long bandOldId, Element parentElement) {
        if (parentElement == null) {
            return;
        }
        BandDao fromBand = this.bandService.fetchBandByOldId(bandOldId);

        String toBandName = parentElement.getText();
        Long toBandOldId = Long.parseLong(parentElement.getAttributeValue("id"));
        BandDao toBand = this.bandService.fetchBandByOldId(toBandOldId);

        BandRelationshipDao relationship = new BandRelationshipDao();
        relationship.setRightBand(fromBand);
        relationship.setRightBandName(fromBand.getName());
        relationship.setLeftBand(toBand);
        relationship.setLeftBandName(toBandName);
        relationship.setRelationship(this.bandService.fetchIsParentOfRelationship());

        relationship.setCreatedBy(this.createUser("tjs"));
        relationship.setUpdatedBy(this.createUser("tjs"));
        relationship.setCreated(LocalDateTime.now());
        relationship.setUpdated(LocalDateTime.now());

        this.bandService.saveRelationship(relationship);
    }

    private void createPreviousName(BandDao band, Element oldNameElement) {
        BandPreviousNameDao previousName = new BandPreviousNameDao();
        previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner")));
        previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy")));
        previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
        previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
        previousName.setOldName(oldNameElement.getChildText("name"));
        previousName.setStartDate(this.notBlankDate(oldNameElement, "start"));
        previousName.setEndDate(this.notBlankDate(oldNameElement, "end"));
        previousName.setHidden(this.notBlankBoolean(oldNameElement, "hidden"));

        this.bandService.createPreviousName(band, previousName);
    }

    private void createBandRehearsalNight(BandDao band, String rehearsalNight) {
        if (rehearsalNight != null) {
            this.bandService.createRehearsalNight(band, RehearsalDay.fromName(rehearsalNight));
        }
    }

    private String[] fetchDirectories() {
        File topLevel = new File(BASE_PATH + "/Bands");
        return Arrays.stream(Objects.requireNonNull(topLevel.list((current, name) -> new File(current, name).isDirectory()))).sorted().toArray(String[]::new);
    }
}
