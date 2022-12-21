package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.venues.dao.VenueAlternativeNameDao;
import uk.co.bbr.services.venues.dao.VenueDao;

public interface VenueAlternativeNameRepository extends JpaRepository<VenueAlternativeNameDao, Long> {
}
