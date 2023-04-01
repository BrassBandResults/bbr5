package uk.co.bbr.services.contests.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;

import java.util.List;

public interface ContestResultPieceRepository extends JpaRepository<ContestResultPieceDao, Long> {
    @Query("SELECT p FROM ContestResultPieceDao p " +
            "WHERE p.contestResult.id = :contestResultId " +
            "ORDER BY p.ordering")
    List<ContestResultPieceDao> fetchForResult(Long contestResultId);

    @Query("SELECT p FROM ContestResultPieceDao p " +
            "WHERE p.piece.id = :pieceId " +
            "ORDER BY p.contestResult.contestEvent.eventDate DESC")
    List<ContestResultPieceDao> fetchForPiece(Long pieceId);
}
