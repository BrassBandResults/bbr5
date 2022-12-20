package uk.co.bbr.services.pieces;

import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceAlternativeNameDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.types.PieceCategory;

import java.util.List;

public interface PieceService {
    PieceDao create(PieceDao newPiece);

    PieceDao create(String name, PieceCategory category, PersonDao composer);

    void createAlternativeName(PieceDao piece, PieceAlternativeNameDao alternativeName);

    PieceDao fetchBySlug(String pieceSlug);

    PieceDao fetchById(Long pieceId);

    List<PieceAlternativeNameDao> fetchAlternateNames(PieceDao piece);

    PieceListDto listPiecesStartingWith(String letter);
}
