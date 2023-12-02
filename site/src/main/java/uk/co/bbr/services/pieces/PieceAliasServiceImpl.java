package uk.co.bbr.services.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.repo.PieceAliasRepository;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PieceAliasServiceImpl implements PieceAliasService, SlugTools {
    private final PieceAliasRepository pieceAliasRepository;
    private final SecurityService securityService;

    @Override
    @IsBbrMember
    public PieceAliasDao createAlias(PieceDao piece, PieceAliasDao previousName) {
        return this.createAlternativeName(piece, previousName, false);
    }

    private PieceAliasDao createAlternativeName(PieceDao piece, PieceAliasDao previousName, boolean migrating) {
        previousName.setPiece(piece);
        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.pieceAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public List<PieceAliasDao> findAllAliases(PieceDao piece) {
        return this.pieceAliasRepository.findForPieceId(piece.getId());
    }

    @Override
    public List<PieceAliasDao> findVisibleAliases(PieceDao piece) {
        return this.pieceAliasRepository.findVisibleForPieceId(piece.getId());
    }

    @Override
    public Optional<PieceAliasDao> aliasExists(PieceDao piece, String aliasName) {
        String name = piece.simplifyPersonFullName(aliasName);
        return this.pieceAliasRepository.fetchByNameForPiece(piece.getId(), name);
    }

    @Override
    public void showAlias(PieceDao piece, Long aliasId) {
        Optional<PieceAliasDao> previousName = this.pieceAliasRepository.fetchByIdForPiece(piece.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.pieceAliasNotFoundByIds(piece.getSlug(), aliasId);
        }
        previousName.get().setHidden(false);
        this.pieceAliasRepository.saveAndFlush(previousName.get());
    }

    @Override
    public void hideAlias(PieceDao piece, Long aliasId) {
        Optional<PieceAliasDao> previousName = this.pieceAliasRepository.fetchByIdForPiece(piece.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.pieceAliasNotFoundByIds(piece.getSlug(), aliasId);
        }
        previousName.get().setHidden(true);
        this.pieceAliasRepository.saveAndFlush(previousName.get());
    }

    @Override
    public void deleteAlias(PieceDao piece, Long aliasId) {
        Optional<PieceAliasDao> previousName = this.pieceAliasRepository.fetchByIdForPiece(piece.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.pieceAliasNotFoundByIds(piece.getSlug(), aliasId);
        }
        this.pieceAliasRepository.delete(previousName.get());

    }
}
