package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeletePieceController {

    private final PieceService pieceService;

    @IsBbrMember
    @GetMapping("/pieces/{pieceSlug:[\\-a-z\\d]{2,}}/delete")
    public String deletePiece(Model model, @PathVariable("pieceSlug") String pieceSlug) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(pieceSlug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(pieceSlug);
        }

        List<ContestResultPieceDao> ownChoiceResults = this.pieceService.fetchOwnChoicePieceUsage(piece.get());
        List<ContestEventTestPieceDao> setTestContests = this.pieceService.fetchSetTestPieceUsage(piece.get());

        boolean blocked = ownChoiceResults.size() > 0 || setTestContests.size() > 0;

        if (blocked) {
            model.addAttribute("Piece", piece.get());
            model.addAttribute("OwnChoiceResults", ownChoiceResults);
            model.addAttribute("SetTestEvents", setTestContests);

            return "pieces/delete-piece-blocked";
        }

        this.pieceService.delete(piece.get());

        return "redirect:/pieces";
    }
}
