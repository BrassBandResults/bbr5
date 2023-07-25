package uk.co.bbr.map.repo;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.springframework.stereotype.Repository;
import uk.co.bbr.map.dto.Location;

@Repository
public interface LocationRepository extends CosmosRepository<Location, String> {
}
