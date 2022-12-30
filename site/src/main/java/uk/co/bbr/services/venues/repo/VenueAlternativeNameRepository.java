package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bbr.services.venues.dao.VenueAliasDao;

public interface VenueAlternativeNameRepository extends JpaRepository<VenueAliasDao, Long> {
}
