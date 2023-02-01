package uk.co.bbr.services.venues;

import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.dto.VenueListDto;

import java.util.Optional;

public interface VenueService {
    VenueDao create(String name);

    VenueDao create(VenueDao venue);

    VenueDao migrate(VenueDao venue);

    Optional<VenueAliasDao> aliasExists(VenueDao venue, String name);

    VenueAliasDao createAlias(VenueDao venue, VenueAliasDao previousName);
    VenueAliasDao migrateAlias(VenueDao venue, VenueAliasDao previousName);

    Optional<VenueDao> fetchBySlug(String slug);

    VenueListDto listVenuesStartingWith(String prefix);
}
