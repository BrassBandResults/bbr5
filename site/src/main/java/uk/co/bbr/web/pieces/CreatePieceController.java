package uk.co.bbr.web.pieces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.web.people.forms.PersonEditForm;
import uk.co.bbr.web.pieces.forms.PieceEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CreatePieceController {
    private final PieceService pieceService;
    private final PersonService personService;


    @IsBbrMember
    @GetMapping("/create/piece")
    public String createGet(Model model) {

        PersonEditForm editForm = new PersonEditForm();

        model.addAttribute("Form", editForm);

        return "pieces/create";
    }

    @IsBbrMember
    @PostMapping("/create/piece")
    public String createPost(Model model, @Valid @ModelAttribute("Form") PieceEditForm submittedForm, BindingResult bindingResult) {

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            return "pieces/create";
        }

        PieceDao newPiece = new PieceDao();

        newPiece.setName(submittedForm.getName());
        newPiece.setNotes(submittedForm.getNotes());
        newPiece.setYear(submittedForm.getYear());
        newPiece.setCategory(PieceCategory.fromCode(submittedForm.getCategory()));
        Optional<PersonDao> composer = this.personService.fetchBySlug(submittedForm.getComposer());
        if (composer.isPresent()) {
            newPiece.setComposer(composer.get());
        }
        Optional<PersonDao> arranger = this.personService.fetchBySlug(submittedForm.getArranger());
        if (arranger.isPresent()) {
            newPiece.setArranger(arranger.get());
        }

        this.pieceService.create(newPiece);

        return "redirect:/pieces";
    }
}
