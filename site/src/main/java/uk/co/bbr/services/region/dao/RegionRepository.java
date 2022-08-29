package uk.co.bbr.services.region.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.region.dto.RegionListDto;

import java.util.List;
import java.util.Optional;

public interface RegionRepository  extends JpaRepository<RegionDao, Long> {
    @Query("SELECT r FROM RegionDao r WHERE r.slug = 'unknown'")
    RegionDao fetchUnknownRegion();
    @Query("SELECT r FROM RegionDao r WHERE r.slug = ?1")
    Optional<RegionDao> findBySlug(String slug);
    @Query("SELECT new uk.co.bbr.services.region.dto.RegionListDto(r, 0) FROM RegionDao r ORDER BY r.name")
    List<RegionListDto> fetchRegionsForList();

    @Query("SELECT COUNT(b) FROM BandDao b WHERE b.status <> uk.co.bbr.services.band.types.BandStatus.EXTINCT AND b.region.slug = ?1")
    int countActiveForRegion(String regionSlug);

    @Query("SELECT COUNT(b) FROM BandDao b WHERE b.status = uk.co.bbr.services.band.types.BandStatus.EXTINCT AND b.region.slug = ?1")
    int countExtinctForRegion(String regionSlug);
}