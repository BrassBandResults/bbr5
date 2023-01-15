package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;

import java.util.Optional;

public interface BandPreviousNameRepository extends JpaRepository<BandPreviousNameDao, Long> {
    @Query("SELECT a FROM BandPreviousNameDao a WHERE a.band.id = ?1 AND a.oldName = ?2")
    Optional<BandPreviousNameDao> findByNameForBand(Long bandId, String aliasName);
}
