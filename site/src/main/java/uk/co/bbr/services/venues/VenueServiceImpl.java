package uk.co.bbr.services.venues;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.repo.VenueRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService, SlugTools {

    private final VenueRepository venueRepository;
    private final SecurityService securityService;

    @Override
    public VenueDao create(VenueDao venue) {
        // validation
        if (venue.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        this.validateMandatory(venue);

        // does the slug already exist?
        Optional<VenueDao> slugMatches = this.venueRepository.findBySlug(venue.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Venue with slug " + venue.getSlug() + " already exists.");
        }

        venue.setCreated(LocalDateTime.now());
        venue.setCreatedBy(this.securityService.getCurrentUserId());
        return this.venueRepository.saveAndFlush(venue);
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

    @Override
    public VenueDao create(String name) {
        VenueDao newVenue = new VenueDao();
        newVenue.setName(name);

        return this.create(newVenue);
    }
}
