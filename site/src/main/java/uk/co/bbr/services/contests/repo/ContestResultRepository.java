package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestResultDao;

import java.util.List;
import java.util.Optional;

public interface ContestResultRepository extends JpaRepository<ContestResultDao, Long> {
    @Query("SELECT r FROM ContestResultDao r " +
            "INNER JOIN BandDao b ON b.id = r.band.id " +
            "INNER JOIN PersonDao c1 ON c1.id = r.conductor.id " +
            "INNER JOIN PersonDao c2 ON c2.id = r.conductorSecond.id " +
            "INNER JOIN PersonDao c3 ON c3.id = r.conductorThird.id " +
            "WHERE r.contestEvent.id = :eventId")
    List<ContestResultDao> findAllForEvent(Long eventId);

    @Query("SELECT r FROM ContestResultDao r WHERE r.contestEvent.id = :eventId AND r.band.id = :bandId")
    Optional<ContestResultDao> fetchForEventAndBand(Long eventId, Long bandId);

    @Query("SELECT r FROM ContestResultDao r " +
            "WHERE r.band.id = :bandId " +
            "AND r.contestEvent.contest.name NOT LIKE '%Whit Friday%' " +
            "ORDER BY r.contestEvent.eventDate DESC")
    List<ContestResultDao> findNonWhitForBand(Long bandId);

    @Query("SELECT r FROM ContestResultDao r " +
            "WHERE r.band.id = :bandId " +
            "AND r.contestEvent.contest.name LIKE '%Whit Friday%' " +
            "ORDER BY r.contestEvent.eventDate DESC")
    List<ContestResultDao> findWhitForBand(Long bandId);
}
