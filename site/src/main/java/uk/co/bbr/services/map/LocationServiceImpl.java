package uk.co.bbr.services.map;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.map.dto.Location;
import uk.co.bbr.services.map.dto.LocationPoint;
import uk.co.bbr.services.map.repo.LocationRepository;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.List;

@Service
@Primary
@Profile("prod")
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService, SlugTools {

    private final LocationRepository locationRepository;

    @Override
    public void updateBandLocation(BandDao band) {
        if (band.hasLocation()) {
            Location location = band.asLocation();
            this.locationRepository.save(location);
        }
    }

    @Override
    public void updateVenueLocation(VenueDao venue) {
        if (venue.hasLocation()) {
            Location location = venue.asLocation();
            this.locationRepository.save(location);
        }
    }

    @Override
    public List<Location> fetchLocationsNear(String latitude, String longitude, int distanceKm) {
        int distanceMetres = distanceKm * 1000; // convert km to metres
        LocationPoint point = new LocationPoint(longitude, latitude);
        return this.locationRepository.fetchLocationsNear(point, distanceMetres);
    }
}
