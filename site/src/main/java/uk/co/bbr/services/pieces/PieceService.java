package uk.co.bbr.services.pieces;

import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceAlias;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.types.PieceCategory;

import java.util.List;

public interface PieceService {
    PieceDao create(PieceDao newPiece);
    PieceDao migrate(PieceDao piece);

    PieceDao create(String name, PieceCategory category, PersonDao composer);
    PieceDao create(String name);

    void createAlternativeName(PieceDao piece, PieceAlias alternativeName);

    void migrateAlternativeName(PieceDao piece, PieceAlias previousName);

    PieceDao fetchBySlug(String pieceSlug);

    PieceDao fetchById(Long pieceId);

    List<PieceAlias> fetchAlternateNames(PieceDao piece);

    PieceListDto listPiecesStartingWith(String letter);




}
