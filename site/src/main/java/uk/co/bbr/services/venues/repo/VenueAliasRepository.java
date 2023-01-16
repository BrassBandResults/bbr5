package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.venues.dao.VenueAliasDao;

import java.util.Optional;

public interface VenueAliasRepository extends JpaRepository<VenueAliasDao, Long> {
    @Query("SELECT a FROM VenueAliasDao a WHERE a.venue.id = ?1 AND a.name = ?2")
    Optional<VenueAliasDao> findByVenueAndName(Long venueId, String name);
}
