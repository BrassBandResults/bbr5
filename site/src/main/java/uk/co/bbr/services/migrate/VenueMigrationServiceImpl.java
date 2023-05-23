package uk.co.bbr.services.migrate;

import lombok.RequiredArgsConstructor;
import org.jdom2.Element;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@IgnoreCoverage
public class VenueMigrationServiceImpl extends AbstractMigrationServiceImpl implements VenueMigrationService, SlugTools {

    private final SecurityService securityService;
    private final VenueService venueService;
    private final RegionService regionService;

    @Override
    public void migrate(Element rootNode, int pass) {

        if (pass == 1) {
            VenueDao venue = new VenueDao();
            venue.setOldId(rootNode.getAttributeValue("id"));
            venue.setSlug(rootNode.getChildText("slug"));
            venue.setName(rootNode.getChildText("name"));
            venue.setNotes(rootNode.getChildText("notes"));

            String regionSlug = rootNode.getChild("country").getAttributeValue("slug");
            if (regionSlug != null && regionSlug.trim().length() > 0) {
                Optional<RegionDao> region = this.regionService.fetchBySlug(regionSlug);
                if (region.isEmpty()) {
                    throw NotFoundException.regionNotFoundBySlug(regionSlug);
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
        } else {
            // second pass - sort out the parents
            Element parentNode = rootNode.getChild("parent");
            if (parentNode != null) {
                String parentSlug = parentNode.getAttributeValue("slug");
                if (parentSlug != null && parentSlug.length() > 0) {
                    Optional<VenueDao> parentVenue = this.venueService.fetchBySlug(parentSlug);
                    if (parentVenue.isEmpty()) {
                        System.out.println("*** Parent not found " + parentSlug);
                        throw new NotFoundException("Parent not found " + parentSlug);
                    }

                    Optional<VenueDao> thisVenue = this.venueService.fetchBySlug(rootNode.getChildText("slug"));
                    if (thisVenue.isEmpty()) {
                        System.out.println("*** Venue to update with parent not found " + parentSlug);
                        throw new NotFoundException("Venue to update with parent not found " + parentSlug);
                    }

                    thisVenue.get().setParent(parentVenue.get());
                    VenueDao venue = this.venueService.update(thisVenue.get());

                    System.out.println(venue.getName());
                }
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
