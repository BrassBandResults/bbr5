package uk.co.bbr.services.venues;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.venues.repo.VenueAliasRepository;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueAliasServiceImpl implements VenueAliasService, SlugTools {
    private final VenueAliasRepository venueAliasRepository;
    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public VenueAliasDao createAlias(VenueDao venue, VenueAliasDao previousName) {
        return this.createAlternativeName(venue, previousName, false);
    }

    private VenueAliasDao createAlternativeName(VenueDao venue, VenueAliasDao previousName, boolean migrating) {
        previousName.setVenue(venue);
        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.venueAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public List<VenueAliasDao> findAllAliases(VenueDao venue) {
        return this.venueAliasRepository.findByVenue(venue.getId());
    }

    @Override
    public Optional<VenueAliasDao> aliasExists(VenueDao venue, String aliasName) {
        String name = venue.simplifyPersonFullName(aliasName);
        return this.venueAliasRepository.fetchByNameForVenue(venue.getId(), name);
    }

    @Override
    public void deleteAlias(VenueDao venue, Long aliasId) {
        Optional<VenueAliasDao> previousName = this.venueAliasRepository.fetchByIdForVenue(venue.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.venueAliasNotFoundByIds(venue.getSlug(), aliasId);
        }
        this.venueAliasRepository.delete(previousName.get());

    }
}
