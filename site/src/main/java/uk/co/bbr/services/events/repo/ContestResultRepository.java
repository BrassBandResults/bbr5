package uk.co.bbr.services.events.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.events.dao.ContestResultDao;

import java.util.List;
import java.util.Optional;

public interface ContestResultRepository extends JpaRepository<ContestResultDao, Long> {
    @Query("SELECT r FROM ContestResultDao r WHERE r.contestEvent.id = :eventId AND r.band.id = :bandId AND r.bandName = :bandName")
    Optional<ContestResultDao> fetchForEventAndBand(Long eventId, Long bandId, String bandName);

    @Query("SELECT r FROM ContestResultDao r WHERE r.band.id = :bandId")
    List<ContestResultDao> findAllForBand(Long bandId);

    @Query("SELECT COUNT(r) FROM ContestResultDao r")
    int countResults();

    @Query("SELECT COUNT(r) FROM ContestResultDao r WHERE r.resultPositionType = uk.co.bbr.services.events.types.ResultPositionType.RESULT")
    int countResultsWithPlacings();
}
