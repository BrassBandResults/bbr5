package uk.co.bbr.services.map;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.List;

public interface LocationService {

    void updateBandLocation(BandDao band);

    void updateVenueLocation(VenueDao venue);
}

