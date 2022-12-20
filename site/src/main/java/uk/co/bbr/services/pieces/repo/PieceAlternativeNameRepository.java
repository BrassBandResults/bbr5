package uk.co.bbr.services.pieces.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.pieces.dao.PieceAlternativeNameDao;

import java.util.List;

public interface PieceAlternativeNameRepository extends JpaRepository<PieceAlternativeNameDao, Long> {

    @Query("SELECT a FROM PieceAlternativeNameDao a WHERE a.piece.id = ?1")
    List<PieceAlternativeNameDao> findForPieceId(Long personId);
}
