package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;

public interface BandPreviousNameRepository extends JpaRepository<BandPreviousNameDao, Long> {
}
