package uk.co.bbr.services.map.repo;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import uk.co.bbr.services.map.dto.Location;

@Primary
public interface LocationRepository extends CosmosRepository<Location, String> {
}
