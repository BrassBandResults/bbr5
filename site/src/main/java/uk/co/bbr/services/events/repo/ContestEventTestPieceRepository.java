package uk.co.bbr.services.events.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;

import java.util.List;

public interface ContestEventTestPieceRepository extends JpaRepository<ContestEventTestPieceDao, Long> {
    @Query("SELECT cp FROM ContestEventTestPieceDao cp " +
            "INNER JOIN PieceDao p ON p.id = cp.piece.id " +
            "WHERE cp.contestEvent.id = ?1")
    List<ContestEventTestPieceDao> fetchForEvent(Long eventId);

    @Query("SELECT cp FROM ContestEventTestPieceDao cp " +
            "INNER JOIN PieceDao p ON p.id = cp.piece.id " +
            "INNER JOIN ContestEventDao c ON c.id = cp.contestEvent.id " +
            "WHERE cp.piece.id = ?1 " +
            "ORDER BY c.eventDate DESC")
    List<ContestEventTestPieceDao> fetchForPiece(Long pieceId);
}
