package uk.co.bbr.services.pieces.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;

import java.util.List;
import java.util.Optional;

public interface PieceAliasRepository extends JpaRepository<PieceAliasDao, Long> {

    @Query("SELECT a FROM PieceAliasDao a WHERE a.piece.id = :pieceId")
    List<PieceAliasDao> findForPieceId(Long pieceId);

    @Query("SELECT a FROM PieceAliasDao a WHERE a.hidden = false AND a.piece.id = :pieceId")
    List<PieceAliasDao> findVisibleForPieceId(Long pieceId);

    @Query("SELECT a FROM PieceAliasDao a WHERE a.piece.id = :pieceId AND a.name = :aliasName")
    Optional<PieceAliasDao> fetchByNameForPiece(Long pieceId, String aliasName);

    @Query("SELECT a FROM PieceAliasDao a WHERE a.piece.id = :pieceId AND a.id = :aliasId")
    Optional<PieceAliasDao> fetchByIdForPiece(Long pieceId, Long aliasId);
}
