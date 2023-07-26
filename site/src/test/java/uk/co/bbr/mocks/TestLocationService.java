package uk.co.bbr.mocks;

import uk.co.bbr.map.LocationService;
import uk.co.bbr.services.bands.dao.BandDao;

public class TestLocationService implements LocationService {
    @Override
    public void updateBandLocation(BandDao band) {
        return;
    }
}
