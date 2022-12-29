package uk.co.bbr.services.regions.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.List;
import java.util.Optional;

public interface RegionRepository  extends JpaRepository<RegionDao, Long> {
    @Query("SELECT r FROM RegionDao r WHERE r.slug = 'unknown'")
    RegionDao fetchUnknownRegion();
    @Query("SELECT r FROM RegionDao r WHERE r.slug = ?1")
    Optional<RegionDao> findBySlug(String slug);
    @Query("SELECT r FROM RegionDao r ORDER BY r.name")
    List<RegionDao> fetchAll();

    @Query("SELECT b FROM BandDao b " +
            "LEFT OUTER JOIN b.section s " +
            "INNER JOIN b.region r " +
            "WHERE r.id = ?1 " +
            "AND b.status <> 0 " +
            "ORDER BY s.position, b.name")
    List<BandDao> findActiveBandsBySection(Long regionId);

    @Query("SELECT b FROM BandDao b " +
            "LEFT OUTER JOIN b.section s " +
            "INNER JOIN b.region r " +
            "WHERE r.slug = ?1 ORDER BY b.name")
    List<BandDao> findBandsForRegion(String regionSlug);

    @Query("SELECT r FROM RegionDao r WHERE r.containerRegionId = ?1")
    List<RegionDao> fetchSubRegionsOf(Long parentRegionId);

    @Query("SELECT b FROM BandDao b " +
            "INNER JOIN b.region r " +
            "WHERE r.id = ?1 " +
            "AND b.latitude IS NOT NULL " +
            "AND b.longitude IS NOT NULL")
    List<BandDao> fetchBandsForMapForRegion(Long regionId);
}