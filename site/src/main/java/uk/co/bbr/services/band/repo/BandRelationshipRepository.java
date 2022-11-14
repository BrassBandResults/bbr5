package uk.co.bbr.services.band.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.dao.BandRelationshipDao;
import uk.co.bbr.services.band.dao.BandRelationshipTypeDao;

public interface BandRelationshipRepository extends JpaRepository<BandRelationshipDao, Long> {
}
