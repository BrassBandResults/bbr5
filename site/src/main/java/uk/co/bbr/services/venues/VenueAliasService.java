package uk.co.bbr.services.venues;

import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.List;
import java.util.Optional;

public interface VenueAliasService {
    VenueAliasDao createAlias(VenueDao person, VenueAliasDao previousName);
    List<VenueAliasDao> findAllAliases(VenueDao person);
    Optional<VenueAliasDao> aliasExists(VenueDao person, String aliasName);
    void deleteAlias(VenueDao person, Long aliasId);
}
