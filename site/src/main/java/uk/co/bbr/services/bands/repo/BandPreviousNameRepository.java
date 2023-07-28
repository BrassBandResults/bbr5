package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandAliasDao;

import java.util.List;
import java.util.Optional;

public interface BandPreviousNameRepository extends JpaRepository<BandAliasDao, Long> {
    @Query("SELECT a FROM BandAliasDao a WHERE a.band.id = :bandId AND a.oldName = :aliasName")
    Optional<BandAliasDao> fetchByNameForBand(Long bandId, String aliasName);

    @Query("SELECT a FROM BandAliasDao a WHERE a.band.id = :bandId AND a.hidden = FALSE ORDER BY a.startDate, a.oldName")
    List<BandAliasDao> findVisibleForBandOrderByName(Long bandId);

    @Query("SELECT a FROM BandAliasDao a WHERE a.band.id = :bandId ORDER BY a.startDate, a.oldName")
    List<BandAliasDao> findAllForBandOrderByName(Long bandId);

    @Query("SELECT a FROM BandAliasDao a WHERE a.band.id = :bandId AND a.id = :aliasId")
    Optional<BandAliasDao> fetchByIdForBand(Long bandId, Long aliasId);
}
