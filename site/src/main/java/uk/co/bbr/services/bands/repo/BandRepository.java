package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandDao;

import java.util.List;
import java.util.Optional;

public interface BandRepository extends JpaRepository<BandDao, Long> {
    @Query("SELECT b FROM BandDao b WHERE b.slug = :bandSlug")
    Optional<BandDao> fetchBySlug(String bandSlug);

    @Query("SELECT b FROM BandDao b WHERE b.oldId = :bandOldId")
    Optional<BandDao> fetchByOldId(String bandOldId);

    @Query("SELECT b FROM BandDao b WHERE b.id = :bandId")
    Optional<BandDao> fetchById(Long bandId);

    @Query("SELECT COUNT(b) FROM BandDao b")
    int countBands();

    @Query("SELECT COUNT(b) FROM BandDao b WHERE LENGTH(b.website) > 2")
    int countBandsWithWebsite();

    @Query("SELECT COUNT(b) FROM BandDao b WHERE LENGTH(b.longitude) > 0 AND LENGTH(b.latitude) > 0")
    int countBandsOnMap();

    @Query("SELECT COUNT(b) FROM BandDao b WHERE LENGTH(b.longitude) > 0 AND LENGTH(b.latitude) > 0 AND b.status = uk.co.bbr.services.bands.types.BandStatus.EXTINCT")
    int countExtinctBandsOnMap();

    @Query("SELECT b FROM BandDao b WHERE b.id = (SELECT MAX(b1.id) FROM BandDao b1)")
    BandDao fetchLatestBand();
}
