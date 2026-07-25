package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dao.PieceHistoryDao;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PieceHistoryController {

    private final PieceService pieceService;

    @IsBbrSuperuser
    @GetMapping("/pieces/{slug:[\\-_a-z\\d]{2,}}/history")
    public String pieceHistory(Model model, @PathVariable("slug") String slug) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(slug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(slug);
        }

        List<PieceHistoryDao> history = this.pieceService.fetchHistory(piece.get());

        model.addAttribute("Piece", piece.get());
        model.addAttribute("History", history);

        return "pieces/history";
    }
}
