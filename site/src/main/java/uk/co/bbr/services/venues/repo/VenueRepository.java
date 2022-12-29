package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<VenueDao, Long> {
    @Query("SELECT v FROM VenueDao v WHERE v.slug = ?1")
    Optional<VenueDao> findBySlug(String venueSlug);
}
