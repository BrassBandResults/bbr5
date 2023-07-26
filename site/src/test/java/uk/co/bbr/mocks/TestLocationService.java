package uk.co.bbr.mocks;

import uk.co.bbr.map.LocationService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.venues.dao.VenueDao;

public class TestLocationService implements LocationService {
    @Override
    public void updateBandLocation(BandDao band) { }

    @Override
    public void updateVenueLocation(VenueDao venue) { }
}
