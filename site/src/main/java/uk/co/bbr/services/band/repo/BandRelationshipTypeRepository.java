package uk.co.bbr.services.band.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.dao.BandRelationshipTypeDao;

public interface BandRelationshipTypeRepository extends JpaRepository<BandRelationshipTypeDao, Long> {
    @Query("SELECT t FROM BandRelationshipTypeDao t WHERE t.name = 'Is Parent Of'")
    BandRelationshipTypeDao fetchIsParentOfRelationship();
}
