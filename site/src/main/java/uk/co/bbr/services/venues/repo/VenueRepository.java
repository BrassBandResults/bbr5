package uk.co.bbr.services.venues.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<VenueDao, Long> {

    @Query("SELECT b FROM VenueDao b ORDER BY b.name")
    List<VenueDao> findAll();
    @Query("SELECT b FROM VenueDao b WHERE UPPER(b.name) LIKE UPPER(CONCAT(:prefix, '%')) ORDER BY b.name")
    List<VenueDao> findByPrefixOrderByName(String prefix);

    @Query("SELECT b FROM VenueDao b " +
            "WHERE b.name LIKE '0%'" +
            "OR b.name LIKE '1%' " +
            "OR b.name LIKE '2%' " +
            "OR b.name LIKE '3%' " +
            "OR b.name LIKE '4%' " +
            "OR b.name LIKE '5%' " +
            "OR b.name LIKE '6%' " +
            "OR b.name LIKE '7%' " +
            "OR b.name LIKE '8%' " +
            "OR b.name LIKE '9%' " +
            "OR b.name LIKE '(%' " +
            "ORDER BY b.name")
    List<VenueDao> findWithNumberPrefixOrderByName();

    @Query("SELECT v FROM VenueDao v WHERE v.slug = ?1")
    Optional<VenueDao> fetchBySlug(String venueSlug);
}
