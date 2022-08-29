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
    @Query("SELECT new uk.co.bbr.services.region.dto.RegionListDto(r) FROM RegionDao r ORDER BY r.name")
    List<RegionListDto> fetchRegionsForList();
}