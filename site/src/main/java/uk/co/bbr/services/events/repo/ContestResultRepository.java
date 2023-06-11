package uk.co.bbr.services.events.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.events.dao.ContestResultDao;

import java.util.List;
import java.util.Optional;

public interface ContestResultRepository extends JpaRepository<ContestResultDao, Long> {
    @Query("SELECT r FROM ContestResultDao r " +
            "INNER JOIN BandDao b ON b.id = r.band.id " +
            "LEFT OUTER JOIN PersonDao c1 ON c1.id = r.conductor.id " +
            "LEFT OUTER JOIN PersonDao c2 ON c2.id = r.conductorSecond.id " +
            "LEFT OUTER JOIN PersonDao c3 ON c3.id = r.conductorThird.id " +
            "WHERE r.contestEvent.id = :eventId")
    List<ContestResultDao> findAllForEvent(Long eventId);

    @Query("SELECT r FROM ContestResultDao r WHERE r.contestEvent.id = :eventId AND r.band.id = :bandId")
    Optional<ContestResultDao> fetchForEventAndBand(Long eventId, Long bandId);

    @Query("SELECT r FROM ContestResultDao r " +
            "WHERE r.band.id = :bandId")
    List<ContestResultDao> findAllForBand(Long bandId);
}