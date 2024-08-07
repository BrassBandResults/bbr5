package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;

import java.util.List;

public interface BandRelationshipTypeRepository extends JpaRepository<BandRelationshipTypeDao, Long> {
    @Query("SELECT t FROM BandRelationshipTypeDao t WHERE t.name = 'relationship.band.is-parent-of'")
    BandRelationshipTypeDao fetchIsParentOfRelationship();

    @Query("SELECT t FROM BandRelationshipTypeDao t ORDER BY t.name")
    List<BandRelationshipTypeDao> findAllOrderByName();
}
