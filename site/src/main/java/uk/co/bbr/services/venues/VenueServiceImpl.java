package uk.co.bbr.services.venues;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.repo.VenueAliasRepository;
import uk.co.bbr.services.venues.repo.VenueRepository;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService, SlugTools {

    private final VenueRepository venueRepository;
    private final VenueAliasRepository venueAliasRepository;
    private final SecurityService securityService;

    @Override
    public VenueDao create(String name) {
        VenueDao newVenue = new VenueDao();
        newVenue.setName(name);

        return this.create(newVenue);
    }

    @Override
    @IsBbrMember
    public VenueDao create(VenueDao venue) {
        return this.create(venue, false);
    }

    @Override
    @IsBbrAdmin
    public VenueDao migrate(VenueDao venue) {
        return this.create(venue, true);
    }

    private VenueDao create(VenueDao venue, boolean migrating) {
        // validation
        if (venue.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        this.validateMandatory(venue);

        // does the slug already exist?
        Optional<VenueDao> slugMatches = this.venueRepository.fetchBySlug(venue.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Venue with slug " + venue.getSlug() + " already exists.");
        }

        if (!migrating) {
            venue.setCreated(LocalDateTime.now());
            venue.setCreatedBy(this.securityService.getCurrentUser());
            venue.setUpdated(LocalDateTime.now());
            venue.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.venueRepository.saveAndFlush(venue);
    }

    @Override
    public Optional<VenueAliasDao> aliasExists(VenueDao venue, String name) {
        return this.venueAliasRepository.findByVenueAndName(venue.getId(), name);
    }

    @Override
    @IsBbrAdmin
    public VenueAliasDao createAlias(VenueDao venue, VenueAliasDao previousName) {
        return this.createAlias(venue, previousName, false);
    }

    @Override
    @IsBbrAdmin
    public VenueAliasDao migrateAlias(VenueDao venue, VenueAliasDao previousName) {
        return this.createAlias(venue, previousName, true);
    }

    private VenueAliasDao createAlias(VenueDao venue, VenueAliasDao previousName, boolean migrating) {
        previousName.setVenue(venue);

        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUser());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUser());
        }

        return this.venueAliasRepository.saveAndFlush(previousName);
    }

    private void validateMandatory(VenueDao venue){
        if (StringUtils.isBlank(venue.getName())) {
            throw new ValidationException("Venue name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(venue.getSlug())) {
            venue.setSlug(slugify(venue.getName()));
        }
    }


}
