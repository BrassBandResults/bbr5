package uk.co.bbr.services.pieces.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.pieces.dao.PieceAlias;

import java.util.List;

public interface PieceAlternativeNameRepository extends JpaRepository<PieceAlias, Long> {

    @Query("SELECT a FROM PieceAlias a WHERE a.piece.id = ?1")
    List<PieceAlias> findForPieceId(Long personId);
}
