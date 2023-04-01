package uk.co.bbr.services.pieces;

import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.types.PieceCategory;

import java.util.List;
import java.util.Optional;

public interface PieceService {
    PieceDao create(PieceDao newPiece);
    PieceDao migrate(PieceDao piece);

    PieceDao create(String name, PieceCategory category, PersonDao composer);
    PieceDao create(String name);

    void createAlternativeName(PieceDao piece, PieceAliasDao alternativeName);

    void migrateAlternativeName(PieceDao piece, PieceAliasDao previousName);

    Optional<PieceDao> fetchBySlug(String pieceSlug);

    Optional<PieceDao> fetchById(Long pieceId);

    List<PieceAliasDao> fetchAlternateNames(PieceDao piece);

    PieceListDto listPiecesStartingWith(String letter);

    List<PieceDao> findPiecesForPerson(PersonDao person);

    List<ContestResultPieceDao> fetchOwnChoicePieceUsage(PieceDao piece);

    List<ContestEventTestPieceDao> fetchSetTestPieceUsage(PieceDao piece);
}
