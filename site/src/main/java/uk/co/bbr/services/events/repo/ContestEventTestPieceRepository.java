package uk.co.bbr.services.events.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;

import java.util.List;
import java.util.Optional;

public interface ContestEventTestPieceRepository extends JpaRepository<ContestEventTestPieceDao, Long> {
    @Query("SELECT cp FROM ContestEventTestPieceDao cp WHERE cp.contestEvent.id = :eventId ORDER BY cp.id")
    List<ContestEventTestPieceDao> fetchForEvent(Long eventId);

    @Query("SELECT cp FROM ContestEventTestPieceDao  cp WHERE cp.contestEvent.id = :eventId AND cp.id = :eventPieceId")
    Optional<ContestEventTestPieceDao> fetchPieceForEventById(Long eventId, Long eventPieceId);
}
