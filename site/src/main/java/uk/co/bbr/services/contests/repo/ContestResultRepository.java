package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;

import java.util.List;
import java.util.Optional;

public interface ContestResultRepository extends JpaRepository<ContestResultDao, Long> {
    @Query("SELECT r FROM ContestResultDao r " +
            "INNER JOIN BandDao b ON b.id = r.band.id " +
            "INNER JOIN PersonDao c1 ON c1.id = r.conductor.id " +
            "INNER JOIN PersonDao c2 ON c2.id = r.conductorSecond.id " +
            "INNER JOIN PersonDao c3 ON c3.id = r.conductorThird.id " +
            "WHERE r.contestEvent.id = ?1")
    List<ContestResultDao> findAllForEvent(Long eventId);

    @Query("SELECT r FROM ContestResultDao r WHERE r.contestEvent.id = ?1 AND r.band.id = ?2")
    Optional<ContestResultDao> fetchForEventAndBand(Long eventId, Long bandId);
}
