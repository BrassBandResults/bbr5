package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.BestOwnChoiceDto;
import uk.co.bbr.services.pieces.sql.dto.PiecesPerSectionSqlDto;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.web.security.annotations.IsBbrPro;


import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PieceController {

    private final PieceService pieceService;
    private final SectionService sectionService;

    @GetMapping("/pieces/{slug:[\\-a-z\\d]{2,}}")
    public String pieceDetails(Model model, @PathVariable("slug") String slug) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(slug);
        }

        List<PieceAliasDao> pieceAliases = this.pieceService.fetchAlternateNames(piece.get());

        List<ContestResultPieceDao> ownChoiceResults = this.pieceService.fetchOwnChoicePieceUsage(piece.get());
        List<ContestEventTestPieceDao> setTestContests = this.pieceService.fetchSetTestPieceUsage(piece.get());


        model.addAttribute("Piece", piece.get());
        model.addAttribute("OwnChoiceResults", ownChoiceResults);
        model.addAttribute("SetTestContests", setTestContests);
        model.addAttribute("SetTestCount", setTestContests.size());
        model.addAttribute("OwnChoiceCount", ownChoiceResults.size());
        model.addAttribute("PreviousNames", pieceAliases);

        return "pieces/piece";
    }

    @IsBbrPro
    @GetMapping("/pieces/BY-SECTION/{sectionSlug:[\\-a-z\\d]{2,}}")
    public String piecesBySection(Model model, @PathVariable("sectionSlug") String sectionSlug) throws Exception {
        Optional<SectionDao> sectionOptional = this.sectionService.fetchBySlug(sectionSlug);
        if (sectionOptional.isEmpty()) {
            throw NotFoundException.sectionNotFoundBySlug(sectionSlug);
        }

        List<PiecesPerSectionSqlDto> piecesForSection = this.pieceService.fetchPiecesForSection(sectionOptional.get());

        model.addAttribute("PiecesForSection", piecesForSection);
        model.addAttribute("Section", sectionOptional.get());

        return "pieces/by-section";
    }

    @IsBbrPro
    @GetMapping("/pieces/BEST-OWN-CHOICE")
    public String mostSuccessfulOwnChoicePieces(Model model) {

        List<BestOwnChoiceDto> bestPieces = this.pieceService.fetchMostSuccessfulOwnChoice();

        model.addAttribute("BestPieces", bestPieces);

        return "pieces/best-own-choice";
    }
}
