package uk.co.bbr.services.pieces;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.dao.PieceAlias;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.repo.PieceAliasRepository;
import uk.co.bbr.services.pieces.repo.PieceRepository;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PieceServiceImpl implements PieceService, SlugTools {

    private final PieceRepository pieceRepository;
    private final PieceAliasRepository pieceAlternativeNameRepository;
    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public PieceDao create(PieceDao piece) {
        return this.create(piece, false);
    }

    @Override
    @IsBbrAdmin
    public PieceDao migrate(PieceDao piece) {
        return this.create(piece, true);
    }

    private PieceDao create(PieceDao piece, boolean migrating) {
        // validation
        if (piece.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(piece.getName())) {
            throw new ValidationException("Piece name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(piece.getSlug())) {
            piece.setSlug(slugify(piece.getName()));
        }

        if (piece.getCategory() == null) {
            piece.setCategory(PieceCategory.TEST_PIECE);
        }

        // does the slug already exist?
        Optional<PieceDao> slugMatches = this.pieceRepository.fetchBySlug(piece.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Piece with slug " + piece.getSlug() + " already exists.");
        }

        if (!migrating) {
            piece.setCreated(LocalDateTime.now());
            piece.setCreatedBy(this.securityService.getCurrentUser());
            piece.setUpdated(LocalDateTime.now());
            piece.setUpdatedBy(this.securityService.getCurrentUser());
        }
        return this.pieceRepository.saveAndFlush(piece);
    }

    @Override
    @IsBbrMember
    public PieceDao create(String name, PieceCategory category, PersonDao composer) {
        PieceDao newPiece = new PieceDao();
        newPiece.setName(name);
        newPiece.setCategory(category);
        newPiece.setComposer(composer);
        return this.create(newPiece);
    }

    @Override
    @IsBbrMember
    public PieceDao create(String name) {
        PieceDao newPiece = new PieceDao();
        newPiece.setName(name);
        newPiece.setCategory(PieceCategory.TEST_PIECE);
        return this.create(newPiece);
    }

    @Override
    @IsBbrMember
    public void createAlternativeName(PieceDao piece, PieceAlias alternativeName) {
        this.createAlternativeName(piece, alternativeName, false);
    }

    @Override
    public void migrateAlternativeName(PieceDao piece, PieceAlias alternativeName) {
        this.createAlternativeName(piece, alternativeName, true);
    }

    private void createAlternativeName(PieceDao piece, PieceAlias alternativeName, boolean migrating) {
        alternativeName.setPiece(piece);
        if (!migrating) {
            alternativeName.setCreated(LocalDateTime.now());
            alternativeName.setCreatedBy(this.securityService.getCurrentUser());
            alternativeName.setUpdated(LocalDateTime.now());
            alternativeName.setUpdatedBy(this.securityService.getCurrentUser());
        }
        this.pieceAlternativeNameRepository.saveAndFlush(alternativeName);
    }

    @Override
    public Optional<PieceDao> fetchBySlug(String pieceSlug) {
        return this.pieceRepository.fetchBySlug(pieceSlug);
    }

    @Override
    public Optional<PieceDao> fetchById(Long pieceId) {
        return this.pieceRepository.fetchById(pieceId);
    }

    @Override
    public List<PieceAlias> fetchAlternateNames(PieceDao piece) {
        return this.pieceAlternativeNameRepository.findForPieceId(piece.getId());
    }

    @Override
    public PieceListDto listPiecesStartingWith(String prefix) {
        List<PieceDao> piecesToReturn;

        switch (prefix.toUpperCase()) {
            case "ALL" -> piecesToReturn = this.pieceRepository.findAllOrderByName();
            case "0" -> piecesToReturn = this.pieceRepository.findWithNumberPrefixOrderByName();
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                piecesToReturn = this.pieceRepository.findByPrefixOrderByName(prefix.trim().toUpperCase());
            }
        }

        long allBandsCount = this.pieceRepository.count();

        return new PieceListDto(piecesToReturn.size(), allBandsCount, prefix, piecesToReturn);
    }


}
