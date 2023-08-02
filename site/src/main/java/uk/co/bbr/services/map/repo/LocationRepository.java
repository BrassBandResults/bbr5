package uk.co.bbr.services.map.repo;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import org.springframework.context.annotation.Primary;
import uk.co.bbr.services.map.dto.Location;
import uk.co.bbr.services.map.dto.LocationPoint;

import java.util.List;

@Primary
public interface LocationRepository extends CosmosRepository<Location, String> {
    @Query("SELECT * FROM Location l WHERE ST_DISTANCE(@point, l.point) < @distanceMeters")
    List<Location> fetchLocationsNear(LocationPoint point, int distanceMeters);
}
