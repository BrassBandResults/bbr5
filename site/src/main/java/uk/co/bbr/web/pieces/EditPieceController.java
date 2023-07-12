package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.pieces.forms.PieceEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.venues.forms.VenueEditForm;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditPieceController {

    private final PersonService personService;
    private final PieceService pieceService;

    @IsBbrMember
    @GetMapping("/pieces/{pieceSlug:[\\-a-z\\d]{2,}}/edit")
    public String editContestGroupForm(Model model, @PathVariable("pieceSlug") String pieceSlug) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(pieceSlug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(pieceSlug);
        }

        PieceEditForm editForm = new PieceEditForm(piece.get());

        model.addAttribute("Piece", piece.get());
        model.addAttribute("Form", editForm);

        return "pieces/edit";
    }

    @IsBbrMember
    @PostMapping("/pieces/{pieceSlug:[\\-a-z\\d]{2,}}/edit")
    public String editContestGroupSave(Model model, @Valid @ModelAttribute("Form") PieceEditForm submittedPiece, BindingResult bindingResult, @PathVariable("pieceSlug") String pieceSlug) {
        Optional<PieceDao> piece = this.pieceService.fetchBySlug(pieceSlug);
        if (piece.isEmpty()) {
            throw NotFoundException.pieceNotFoundBySlug(pieceSlug);
        }

        submittedPiece.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("Piece", piece.get());
            return "pieces/edit";
        }

        PieceDao existingPiece = piece.get();

        existingPiece.setName(submittedPiece.getName());
        existingPiece.setYear(submittedPiece.getYear());
        existingPiece.setNotes(submittedPiece.getNotes());
        existingPiece.setCategory(PieceCategory.fromCode(submittedPiece.getCategory()));

        if (submittedPiece.getComposerSlug() != null) {
            Optional<PersonDao> composer = this.personService.fetchBySlug(submittedPiece.getComposerSlug());
            if (composer.isPresent()) {
                existingPiece.setComposer(composer.get());
            }
        }
        else {
            existingPiece.setComposer(null);
        }
        if (submittedPiece.getArrangerSlug() != null) {
            Optional<PersonDao> arranger = this.personService.fetchBySlug(submittedPiece.getArrangerSlug());
            if (arranger.isPresent()) {
                existingPiece.setArranger(arranger.get());
            }
        } else {
            existingPiece.setArranger(null);
        }

        this.pieceService.update(existingPiece);

        return "redirect:/pieces/{pieceSlug}";
    }
}
