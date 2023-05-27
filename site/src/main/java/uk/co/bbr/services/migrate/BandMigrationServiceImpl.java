package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@IgnoreCoverage
public class BandMigrationServiceImpl extends AbstractMigrationServiceImpl implements BandMigrationService, SlugTools {

    private final RegionService regionService;
    private final BandService bandService;
    private final BandAliasService bandAliasService;
    private final SectionService sectionService;
    private final SecurityService securityService;

    @Override
    public void migrate(Element rootNode) {
        BandDao newBand = new BandDao();
        newBand.setOldId(rootNode.getAttributeValue("id"));
        newBand.setName(rootNode.getChildText("name"));
        newBand.setSlug(rootNode.getChildText("slug"));
        if (!rootNode.getChildText("website").equals("http://")){
            newBand.setWebsite(this.notBlank(rootNode, "website"));
        }

        newBand.setTwitterName(this.notBlank(rootNode, "twitter"));

        Optional<RegionDao> region = this.regionService.fetchBySlug(rootNode.getChild("region").getAttributeValue("slug"));
        if (region.isEmpty()) {
            throw new NotFoundException("Region not found");
        }
        newBand.setRegion(region.get());

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
            Optional<SectionDao> section = this.sectionService.fetchByName(gradingName);
            if (section.isEmpty()) {
                throw new NotFoundException("Section not found " + gradingName);
            }
            newBand.setSection(section.get());
        }

        newBand.setMapper(this.createUser(this.notBlank(rootNode, "mapper"), this.securityService));
        newBand.setCreatedBy(this.createUser(this.notBlank(rootNode, "owner"), this.securityService));
        newBand.setUpdatedBy(this.createUser(this.notBlank(rootNode, "lastChangedBy"), this.securityService));

        newBand.setCreated(this.notBlankDateTime(rootNode, "created"));
        newBand.setUpdated(this.notBlankDateTime(rootNode, "lastModified"));

        newBand.setNotes(this.notBlank(rootNode, "notes"));

        // notes
        newBand = this.bandService.migrate(newBand);

        this.createBandRehearsalNight(newBand, this.notBlank(rootNode,"rehearsal1"));
        this.createBandRehearsalNight(newBand, this.notBlank(rootNode,"rehearsal2"));

        Element previousNames = rootNode.getChild("previous_names");
        List<Element> previousNameNodes = previousNames.getChildren();
        for (Element eachOldName : previousNameNodes) {
            this.createPreviousName(newBand, eachOldName);
        }

        System.out.println(newBand.getName());
    }

    private void createPreviousName(BandDao band, Element oldNameElement) {
        String name = oldNameElement.getChildText("name");
        // does it already exist?

        Optional<BandAliasDao> existingAlias = this.bandAliasService.aliasExists(band, name);
        if (existingAlias.isEmpty()) {
            BandAliasDao previousName = new BandAliasDao();
            previousName.setCreatedBy(this.createUser(this.notBlank(oldNameElement, "owner"), this.securityService));
            previousName.setUpdatedBy(this.createUser(this.notBlank(oldNameElement, "lastChangedBy"), this.securityService));
            previousName.setCreated(this.notBlankDateTime(oldNameElement, "created"));
            previousName.setUpdated(this.notBlankDateTime(oldNameElement, "lastModified"));
            previousName.setOldName(name);
            previousName.setStartDate(this.notBlankDate(oldNameElement, "start"));
            previousName.setEndDate(this.notBlankDate(oldNameElement, "end"));
            previousName.setHidden(this.notBlankBoolean(oldNameElement, "hidden"));

            if (previousName.getCreatedBy() == null) {
                previousName.setCreatedBy(band.getCreatedBy());
            }
            if (previousName.getUpdatedBy() == null) {
                previousName.setUpdatedBy(band.getUpdatedBy());
            }

            this.bandAliasService.migrateAlias(band, previousName);
        }
    }

    private void createBandRehearsalNight(BandDao band, String rehearsalNight) {
        if (rehearsalNight != null) {
            this.bandService.migrateRehearsalNight(band, RehearsalDay.fromName(rehearsalNight));
        }
    }

    private void createBandLink(String bandOldId, Element parentElement) {
        if (parentElement == null) {
            return;
        }
        Optional<BandDao> fromBandOptional = this.bandService.fetchBandByOldId(bandOldId);

        String toBandName = parentElement.getText();
        String toBandOldId = parentElement.getAttributeValue("id");
        Optional<BandDao> toBandOptional = this.bandService.fetchBandByOldId(toBandOldId);

        if (fromBandOptional.isEmpty() || toBandOptional.isEmpty()) {
            throw new NotFoundException("Can't find bands to link");
        }

        BandDao fromBand = fromBandOptional.get();
        BandDao toBand = toBandOptional.get();

        BandRelationshipDao relationship = new BandRelationshipDao();
        relationship.setRightBand(fromBand);
        relationship.setRightBandName(fromBand.getName());
        relationship.setLeftBand(toBand);
        relationship.setLeftBandName(toBandName);
        relationship.setRelationship(this.bandService.fetchIsParentOfRelationship());

        relationship.setCreatedBy(this.createUser("tjs", this.securityService));
        relationship.setUpdatedBy(this.createUser("tjs", this.securityService));
        relationship.setCreated(LocalDateTime.now());
        relationship.setUpdated(LocalDateTime.now());

        this.bandService.migrateRelationship(relationship);
    }

}
