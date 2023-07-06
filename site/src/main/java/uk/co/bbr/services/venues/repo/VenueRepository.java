package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<VenueDao, Long> {

    @Query("SELECT b FROM VenueDao b ORDER BY b.name")
    List<VenueDao> findAll();
    @Query("SELECT b FROM VenueDao b WHERE UPPER(b.name) LIKE UPPER(CONCAT(:prefix, '%')) ORDER BY b.name")
    List<VenueDao> findByPrefixOrderByName(String prefix);

    @Query("SELECT v FROM VenueDao v WHERE v.slug = ?1")
    Optional<VenueDao> fetchBySlug(String venueSlug);

    @Query("SELECT COUNT(v) FROM VenueDao v")
    int countVenues();

    @Query("SELECT COUNT(v) FROM VenueDao v WHERE LENGTH(v.latitude) > 0 AND LENGTH(v.longitude) > 0")
    int countVenuesOnMap();

    @Query("SELECT v FROM VenueDao v WHERE v.id = (SELECT MAX(v1.id) FROM VenueDao v1)")
    VenueDao fetchLatestVenue();
}
