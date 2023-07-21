package uk.co.bbr.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;

import java.util.Optional;

@RestController
@RequestMapping("/api/pieces")
@RequiredArgsConstructor
public class PieceApiController {

    private final PieceService pieceService;

    @GetMapping("/{slug}")
    public Optional<PieceDao> getPiece(@PathVariable("slug") String slug) {
        return this.pieceService.fetchBySlug(slug);
    }
}
