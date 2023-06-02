package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;

import java.util.List;
import java.util.Optional;

public interface BandRelationshipRepository extends JpaRepository<BandRelationshipDao, Long> {
    @Query("SELECT r FROM BandRelationshipDao r WHERE r.leftBand.id = :bandId OR r.rightBand.id = :bandId ORDER BY r.leftBand.name, r.rightBand.name")
    List<BandRelationshipDao> findForBand(Long bandId);

    @Query("SELECT r FROM BandRelationshipDao r WHERE r.id = :relationshipId")
    Optional<BandRelationshipDao> fetchById(Long relationshipId);
}
