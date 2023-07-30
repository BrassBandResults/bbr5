package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;

@Controller
@RequiredArgsConstructor
public class PiecesListController {

    private final PieceService pieceService;

    @GetMapping("/pieces")
    public String peopleListHome(Model model) {
        return peopleListLetter(model, "A");
    }

    @GetMapping("/pieces/{letter:[A-Z0-9]}")
    public String peopleListLetter(Model model, @PathVariable("letter") String letter) {
        PieceListDto pieces = this.pieceService.listPiecesStartingWith(letter);

        model.addAttribute("PiecePrefixLetter", letter);
        model.addAttribute("Pieces", pieces);
        return "pieces/pieces";
    }

    @IsBbrMember
    @GetMapping("/pieces/ALL")
    public String peopleListAll(Model model) {
        PieceListDto pieces = this.pieceService.listPiecesStartingWith("ALL");

        model.addAttribute("PiecePrefixLetter", "ALL");
        model.addAttribute("Pieces", pieces);
        return "pieces/pieces";
    }
}
