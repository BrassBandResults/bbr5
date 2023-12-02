package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.venues.dao.VenueAliasDao;

import java.util.List;
import java.util.Optional;

public interface VenueAliasRepository extends JpaRepository<VenueAliasDao, Long> {
    @Query("SELECT a FROM VenueAliasDao a WHERE a.venue.id = :venueId AND a.name = :aliasName")
    Optional<VenueAliasDao> findByVenueAndAliasName(Long venueId, String aliasName);

    @Query("SELECT a FROM VenueAliasDao a WHERE a.venue.id = :venueId")
    List<VenueAliasDao> findByVenue(Long venueId);

    @Query("SELECT a FROM VenueAliasDao a WHERE a.venue.id = :venueId AND a.name = :aliasName")
    Optional<VenueAliasDao> fetchByNameForVenue(Long venueId, String aliasName);

    @Query("SELECT a FROM VenueAliasDao a WHERE a.venue.id = :venueId AND a.id = :aliasId")
    Optional<VenueAliasDao> fetchByIdForVenue(Long venueId, Long aliasId);
}
