package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;

import java.util.List;
import java.util.Optional;

public interface BandPreviousNameRepository extends JpaRepository<BandPreviousNameDao, Long> {
    @Query("SELECT a FROM BandPreviousNameDao a WHERE a.band.id = ?1 AND a.oldName = ?2")
    Optional<BandPreviousNameDao> fetchByNameForBand(Long bandId, String aliasName);

    @Query("SELECT a FROM BandPreviousNameDao a WHERE a.band.id = ?1 AND a.hidden = FALSE ORDER BY a.startDate, a.oldName")
    List<BandPreviousNameDao> findVisibleForBandOrderByName(Long bandId);

    @Query("SELECT b FROM BandDao b " +
            "INNER JOIN BandPreviousNameDao a ON a.band.id = b.id " +
            "WHERE UPPER(a.oldName) = :bandNameUpper")
    List<BandDao> findAliasExactNameMatch(String bandNameUpper);

    @Query("SELECT b FROM BandDao b " +
            "INNER JOIN BandPreviousNameDao a ON a.band.id = b.id " +
            "WHERE UPPER(a.oldName) LIKE :bandNameUpper")
    List<BandDao> findContainsNameMatch(String bandNameUpper);
}
