package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;

public interface BandRelationshipRepository extends JpaRepository<BandRelationshipDao, Long> {
}
