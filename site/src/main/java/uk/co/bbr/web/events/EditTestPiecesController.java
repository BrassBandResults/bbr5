package uk.co.bbr.web.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.types.TestPieceAndOr;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.events.forms.AddEventSetTestForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditTestPiecesController {

    private final ContestEventService contestEventService;
    private final PieceService pieceService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-set-tests")
    public String editSetTests(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        AddEventSetTestForm editForm = new AddEventSetTestForm();

        List<ContestEventTestPieceDao> setTestPieces = this.contestEventService.listTestPieces(contestEvent.get());

        model.addAttribute("ContestEvent", contestEvent.get());
        model.addAttribute("Form", editForm);
        model.addAttribute("TestPieces", setTestPieces);

        return "events/edit-set-tests";
    }

    @IsBbrMember
    @PostMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-set-tests")
    public String editSetTestsAdd(Model model, @Valid @ModelAttribute("Form") AddEventSetTestForm submittedForm, BindingResult bindingResult, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<ContestEventTestPieceDao> setTestPieces = this.contestEventService.listTestPieces(contestEvent.get());

            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("TestPieces", setTestPieces);
            return "events/edit-set-tests";
        }

        Optional<PieceDao> piece = this.pieceService.fetchBySlug(submittedForm.getPieceSlug());
        if (piece.isEmpty()) {
            List<ContestEventTestPieceDao> setTestPieces = this.contestEventService.listTestPieces(contestEvent.get());

            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("TestPieces", setTestPieces);
            return "events/edit-set-tests";
        }

        TestPieceAndOr andOr = TestPieceAndOr.fromCode(submittedForm.getAndOr());

        this.contestEventService.addTestPieceToContest(contestEvent.get(), piece.get(), andOr);

        return "redirect:/contests/{contestSlug}/" + contestEvent.get().getEventDateForUrl() + "/edit-set-tests";
    }

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-set-tests/{eventPieceId:\\d+}/delete")
    public String removeSetTest(@PathVariable String contestSlug, @PathVariable String contestEventDate, @PathVariable Long eventPieceId) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);
        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        Optional<ContestEventTestPieceDao> piece = this.contestEventService.fetchSetTestById(contestEvent.get(), eventPieceId);
        if (piece.isEmpty()) {
            throw NotFoundException.setTestPieceNotFoundById();
        }

        this.contestEventService.removeSetTestPiece(piece.get());

        return "redirect:/contests/{contestSlug}/" + contestEvent.get().getEventDateForUrl() + "/edit-set-tests";
    }
}
