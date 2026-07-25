package uk.co.bbr.services.pieces.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.co.bbr.services.pieces.dao.PieceHistoryDao;

import java.util.List;

public interface PieceHistoryRepository extends JpaRepository<PieceHistoryDao, Long> {

    @Query("SELECT ph FROM PieceHistoryDao ph WHERE ph.piece.id = :pieceId ORDER BY ph.updated DESC")
    List<PieceHistoryDao> findForPieceId(@Param("pieceId") Long pieceId);
}
