package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.venues.dao.VenueDao;

public interface VenueRepository extends JpaRepository<VenueDao, Long> {
}
