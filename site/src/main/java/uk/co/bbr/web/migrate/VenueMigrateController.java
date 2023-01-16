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
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class VenueMigrateController extends AbstractMigrateController  {

    private final SecurityService securityService;
    private final VenueService venueService;
    private final RegionService regionService;

    @GetMapping("/migrate/venues")
    @IsBbrAdmin
    public String home(Model model)  {
        List<String> messages = new ArrayList<>();
        messages.add("Cloning venues repository...");

        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/venues/clone");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/venues/clone")
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
        model.addAttribute("Next", "/migrate/venues/0");

        return "migrate/migrate";
    }

    @GetMapping("/migrate/venues/{index}")
    @IsBbrAdmin
    public String clone(Model model, @PathVariable("index") int index) throws GitAPIException, IOException, JDOMException {

        List<String> messages = new ArrayList<>();
        String[] directories = this.fetchDirectories();
        try {
            String indexLetter = directories[index];
            this.importContestVenues(indexLetter);
            messages.add("Processing letter " + indexLetter + "...");
        }
        catch (IndexOutOfBoundsException ex) {
            return "redirect:/";
        }
        
        int nextIndex = index + 1;
        
        model.addAttribute("Messages", messages);
        model.addAttribute("Next", "/migrate/venues/" + nextIndex);

        return "migrate/migrate";
    }

    private void importContestVenues(String indexLetter) throws JDOMException, IOException {
        File letterLevel = new File(BASE_PATH + "/Venues/" + indexLetter);
        if (letterLevel.exists()) {
            String[] files = Arrays.stream(letterLevel.list((current, name) -> new File(current, name).isFile())).sorted().toArray(String[]::new);

            for (String eachFile : files) {
                File eachBandFile = new File(BASE_PATH + "/Venues/" + indexLetter + "/" + eachFile);
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

                VenueDao venue = new VenueDao();
                venue.setOldId(rootNode.getAttributeValue("id"));
                venue.setSlug(rootNode.getChildText("slug"));
                venue.setName(rootNode.getChildText("name"));
                venue.setNotes(rootNode.getChildText("notes"));

                String regionSlug = rootNode.getChild("country").getAttributeValue("slug");
                if (regionSlug != null && regionSlug.trim().length() > 0) {
                    Optional<RegionDao> region = this.regionService.fetchBySlug(regionSlug);
                    if (region.isEmpty()) {
                        throw new NotFoundException("Region not found " + regionSlug);
                    }
                    venue.setRegion(region.get());
                }
                venue.setLatitude(this.notBlank(rootNode, "latitude"));
                venue.setLongitude(this.notBlank(rootNode, "longitude"));
                venue.setExact(this.notBlankBoolean(rootNode, "exact"));
                venue.setMapper(this.createUser(this.notBlank(rootNode, "mapper"), this.securityService));

                venue.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
                venue.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

                venue.setCreated(this.notBlankDateTime(rootNode, "created"));
                venue.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

                venue = this.venueService.migrate(venue);

                // aliases
                Element previousNames = rootNode.getChild("previous_names");
                List<Element> previousNameNodes = previousNames.getChildren();
                for (Element eachOldName : previousNameNodes) {
                    this.createPreviousName(venue, eachOldName);
                }

                System.out.println(venue.getName());
            }
        }
    }

    private void createPreviousName(VenueDao venue, Element oldNameElement) {
        String name = oldNameElement.getChildText("name");
        // does it already exist?

        Optional<VenueAliasDao> existingAlias = this.venueService.aliasExists(venue, name);
        if (existingAlias.isEmpty()) {

            VenueAliasDao previousName = new VenueAliasDao();
            previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
            previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
            previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
            previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
            previousName.setStartDate(this.notBlankDate(oldNameElement, "start"));
            previousName.setEndDate(this.notBlankDate(oldNameElement, "end"));
            previousName.setName(name);

            this.venueService.migrateAlias(venue, previousName);
        }
    }
}
