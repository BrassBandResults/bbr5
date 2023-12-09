package uk.co.bbr.services.pieces;

import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.BestOwnChoiceDto;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.sql.dto.PiecesPerSectionSqlDto;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.sections.dao.SectionDao;

import java.util.List;
import java.util.Optional;

public interface PieceService {
    PieceDao create(PieceDao newPiece);
    PieceDao create(String name, PieceCategory category, PersonDao composer);
    PieceDao create(String name);
    PieceDao update(PieceDao existingPiece);

    void createAlternativeName(PieceDao piece, PieceAliasDao alternativeName);

    Optional<PieceDao> fetchBySlug(String pieceSlug);

    Optional<PieceDao> fetchById(Long pieceId);

    List<PieceAliasDao> fetchAlternateNames(PieceDao piece);

    PieceListDto listPiecesStartingWith(String letter);

    PieceListDto listUnusedPieces();

    List<PieceDao> findPiecesForPerson(PersonDao person);

    List<ContestResultPieceDao> fetchOwnChoicePieceUsage(PieceDao piece);

    List<ContestEventTestPieceDao> fetchSetTestPieceUsage(PieceDao piece);

    List<BestOwnChoiceDto> fetchMostSuccessfulOwnChoice();

    List<PiecesPerSectionSqlDto> fetchPiecesForSection(SectionDao section);

    void delete(PieceDao piece);
}
