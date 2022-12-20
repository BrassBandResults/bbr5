package uk.co.bbr.services.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dto.PeopleListDto;
import uk.co.bbr.services.pieces.dao.PieceAlternativeNameDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.repo.PieceAlternativeNameRepository;
import uk.co.bbr.services.pieces.repo.PieceRepository;
import uk.co.bbr.services.pieces.types.PieceCategory;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PieceServiceImpl implements PieceService, SlugTools {

    private final PieceRepository pieceRepository;
    private final PieceAlternativeNameRepository pieceAlternativeNameRepository;

    @Override
    public PieceDao create(PieceDao newPiece) {
        // validation
        if (newPiece.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (newPiece.getName() == null || newPiece.getName().trim().length() == 0) {
            throw new ValidationException("Piece name must be specified");
        }

        // defaults
        if (newPiece.getSlug() == null || newPiece.getSlug().trim().length() == 0) {
            newPiece.setSlug(slugify(newPiece.getName()));
        }

        if (newPiece.getCategory() == null) {
            newPiece.setCategory(PieceCategory.TEST_PIECE);
        }

        return this.pieceRepository.saveAndFlush(newPiece);
    }

    @Override
    public PieceDao create(String name, PieceCategory category, PersonDao composer) {
        PieceDao newPiece = new PieceDao();
        newPiece.setName(name);
        newPiece.setCategory(category);
        newPiece.setComposer(composer);
        return this.create(newPiece);
    }

    @Override
    public void createAlternativeName(PieceDao piece, PieceAlternativeNameDao alternativeName) {
        alternativeName.setPiece(piece);
        this.pieceAlternativeNameRepository.saveAndFlush(alternativeName);
    }

    @Override
    public PieceDao fetchBySlug(String pieceSlug) {
        Optional<PieceDao> piece = this.pieceRepository.fetchBySlug(pieceSlug);
        if (piece.isEmpty()) {
            throw new UnsupportedOperationException("Piece with slug " + pieceSlug + " not found");
        }
        return piece.get();
    }

    @Override
    public PieceDao fetchById(Long pieceId) {
        Optional<PieceDao> piece = this.pieceRepository.fetchById(pieceId);
        if (piece.isEmpty()) {
            throw new UnsupportedOperationException("Piece with id " + pieceId + " not found");
        }
        return piece.get();
    }

    @Override
    public List<PieceAlternativeNameDao> fetchAlternateNames(PieceDao piece) {
        return this.pieceAlternativeNameRepository.findForPieceId(piece.getId());
    }

    @Override
    public PieceListDto listPiecesStartingWith(String prefix) {
        List<PieceDao> piecesToReturn;

        switch (prefix.toUpperCase()) {
            case "ALL" -> piecesToReturn = this.pieceRepository.findAll();
            case "0" -> piecesToReturn = this.pieceRepository.findWithNumberPrefix();
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                piecesToReturn = this.pieceRepository.findByPrefix(prefix.trim().toUpperCase());
            }
        }

        long allBandsCount = this.pieceRepository.count();

        return new PieceListDto(piecesToReturn.size(), allBandsCount, prefix, piecesToReturn);
    }
}
