package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;


import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PieceController {

    private final PieceService pieceService;

    @GetMapping("/pieces/{slug:[\\-a-z\\d]{2,}}")
    public String pieceDetails(Model model, @PathVariable("slug") String slug) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw new NotFoundException("Piece with slug " + slug + " not found");
        }

        List<PieceAliasDao> pieceAliases = this.pieceService.fetchAlternateNames(piece.get());

        List<ContestResultPieceDao> ownChoiceResults = this.pieceService.fetchOwnChoicePieceUsage(piece.get());
        List<ContestEventTestPieceDao> setTestContests = this.pieceService.fetchSetTestPieceUsage(piece.get());


        model.addAttribute("Piece", piece.get());
        model.addAttribute("OwnChoiceResults", ownChoiceResults);
        model.addAttribute("SetTestContests", setTestContests);
        model.addAttribute("SetTestCount", setTestContests.size());
        model.addAttribute("OwnChoiceCount", ownChoiceResults.size());

        return "pieces/piece";
    }
}
