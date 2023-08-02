package uk.co.bbr.mocks;

import uk.co.bbr.services.map.LocationService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.map.dto.Location;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.Collections;
import java.util.List;

public class TestLocationService implements LocationService {
    @Override
    public void updateBandLocation(BandDao band) { }

    @Override
    public void updateVenueLocation(VenueDao venue) { }

    @Override
    public List<Location> fetchLocationsNear(String latitude, String longitude, int distanceKm) {
        return Collections.emptyList();
    }
}
