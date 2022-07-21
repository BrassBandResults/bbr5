package uk.co.bbr.services.band.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.dao.BandPreviousNameDao;

public interface BandPreviousNameRepository extends JpaRepository<BandPreviousNameDao, Long> {
}
