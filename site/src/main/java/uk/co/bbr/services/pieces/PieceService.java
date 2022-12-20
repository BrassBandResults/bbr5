package uk.co.bbr.services.pieces;

import uk.co.bbr.services.pieces.dao.PieceAlternativeNameDao;
import uk.co.bbr.services.pieces.dao.PieceDao;

import java.util.List;

public interface PieceService {
    PieceDao create(PieceDao newPiece);

    void createAlternativeName(PieceDao piece, PieceAlternativeNameDao alternativeName);

    PieceDao fetchBySlug(String pieceSlug);

    PieceDao fetchById(Long pieceId);

    List<PieceAlternativeNameDao> fetchAlternateNames(PieceDao piece);
}
