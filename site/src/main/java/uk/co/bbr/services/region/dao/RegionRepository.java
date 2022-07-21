package uk.co.bbr.services.region.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegionRepository  extends JpaRepository<RegionDao, Long> {
    @Query("SELECT r FROM RegionDao r WHERE r.slug = 'unknown'")
    RegionDao fetchUnknownRegion();
}
