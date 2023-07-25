package uk.co.bbr.map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.map.dto.Location;
import uk.co.bbr.map.repo.LocationRepository;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService, SlugTools {

    private final LocationRepository locationRepository;

    @Override
    public void updateBandLocation(BandDao band) {
        Location location = band.asLocation();
       this.locationRepository.save(location);
    }
}
