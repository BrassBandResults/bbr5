package uk.co.bbr.services.events.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContestEventRepository extends JpaRepository<ContestEventDao, Long> {
    @Query("SELECT e FROM ContestEventDao e WHERE e.contest.id = :contestId and e.eventDate = :eventDate")
    Optional<ContestEventDao> fetchByContestAndDate(Long contestId, LocalDate eventDate);

    @Query("SELECT r FROM ContestResultDao r " +
            "INNER JOIN BandDao b ON r.band.id = b.id " +
            "WHERE r.contestEvent.id = :contestEventId " +
            "AND r.position = 1")
    List<ContestResultDao> fetchWinningBands(Long contestEventId);

    @Query("SELECT t FROM ContestEventTestPieceDao t " +
            "INNER JOIN PieceDao p on p.id = t.piece.id " +
            "WHERE t.contestEvent.id = :contestEventId " +
            "ORDER BY p.name")
    List<ContestEventTestPieceDao> fetchTestPieces(Long contestEventId);

    @Query("SELECT e FROM ContestEventDao e WHERE e.contest.id = :contestId AND e.eventDate > CURRENT_TIMESTAMP ORDER BY e.eventDate DESC")
    List<ContestEventDao> fetchFutureEventsByContest(Long contestId);

    @Query("SELECT COUNT(e) FROM ContestEventDao e WHERE e.contest.id = :contestId AND e.eventDate < CURRENT_TIMESTAMP")
    int countEventsForContest(Long contestId);

    @Query("SELECT e FROM ContestEventDao e " +
            "WHERE e.venue.id = :venueId " +
            "ORDER BY e.contest.name DESC")
    List<ContestEventDao> fetchEventsForVenue(Long venueId);

    @Query("SELECT e FROM ContestEventDao e " +
            "WHERE e.venue.id = :venueId " +
            "AND YEAR(e.eventDate) = :year " +
            "ORDER BY e.contest.name DESC")
    List<ContestEventDao> fetchEventsForVenueInYear(Long venueId, int year);

    @Query("SELECT e FROM ContestEventDao e " +
            "WHERE e.venue.id = :venueId " +
            "AND e.contest.id = :contestId " +
            "ORDER BY e.contest.name DESC")
    List<ContestEventDao> fetchEventsForVenueForSpecificContest(Long venueId, Long contestId);

    @Query("SELECT COUNT(e) FROM ContestEventDao e")
    int countEvents();
}
