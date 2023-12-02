package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.pieces.PieceAliasService;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PieceAliasController {

    private final PieceService pieceService;
    private final PieceAliasService pieceAliasService;

    private static final String REDIRECT_TO_PIECE_ALIASES = "redirect:/pieces/{slug}/edit-aliases";

    @IsBbrMember
    @GetMapping("/pieces/{slug:[\\-a-z\\d]{2,}}/edit-aliases")
    public String pieceAliasEdit(Model model, @PathVariable("slug") String slug) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(slug);
        }

        List<PieceAliasDao> previousNames = this.pieceAliasService.findAllAliases(piece.get());

        model.addAttribute("Piece", piece.get());
        model.addAttribute("PreviousNames", previousNames);
        return "pieces/piece-aliases";
    }

    @IsBbrMember
    @GetMapping("/pieces/{slug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/hide")
    public String pieceAliasHide(@PathVariable("slug") String slug, @PathVariable("aliasId") Long aliasId) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(slug);
        }

        this.pieceAliasService.hideAlias(piece.get(), aliasId);

        return REDIRECT_TO_PIECE_ALIASES;
    }

    @IsBbrMember
    @GetMapping("/pieces/{slug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/show")
    public String pieceAliasShow(@PathVariable("slug") String slug, @PathVariable("aliasId") Long aliasId) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(slug);
        }

        this.pieceAliasService.showAlias(piece.get(), aliasId);

        return REDIRECT_TO_PIECE_ALIASES;
    }

    @IsBbrMember
    @GetMapping("/pieces/{slug:[\\-a-z\\d]{2,}}/edit-aliases/{aliasId:\\d+}/delete")
    public String pieceAliasDelete(@PathVariable("slug") String slug, @PathVariable("aliasId") Long aliasId) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(slug);
        }

        this.pieceAliasService.deleteAlias(piece.get(), aliasId);

        return REDIRECT_TO_PIECE_ALIASES;
    }

    @IsBbrMember
    @PostMapping("/pieces/{slug:[\\-a-z\\d]{2,}}/edit-aliases/add")
    public String pieceAliasShow(@PathVariable("slug") String slug, @RequestParam("name") String oldName) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(slug);
        }

        PieceAliasDao previousName = new PieceAliasDao();
        previousName.setName(oldName);
        previousName.setHidden(false);
        this.pieceAliasService.createAlias(piece.get(), previousName);

        return REDIRECT_TO_PIECE_ALIASES;
    }
}

