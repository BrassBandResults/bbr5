package uk.co.bbr.services.band.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.band.dao.BandDao;

public interface BandRepository extends JpaRepository<BandDao, Long> {
}
