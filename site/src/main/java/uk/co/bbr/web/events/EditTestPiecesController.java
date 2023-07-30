package uk.co.bbr.web.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.events.forms.AddEventSetTestForm;
import uk.co.bbr.web.events.forms.EventEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditTestPiecesController {

    private final ContestService contestService;
    private final ContestEventService contestEventService;
    private final ContestTypeService contestTypeService;
    private final VenueService venueService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-set-tests")
    public String editSetTests(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
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
    public String editSetTestsAdd(Model model, @Valid @ModelAttribute("Form") EventEditForm submittedEvent, BindingResult bindingResult, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        submittedEvent.validate(bindingResult);

        List<ContestEventTestPieceDao> setTestPieces = this.contestEventService.listTestPieces(contestEvent.get());

        if (bindingResult.hasErrors()) {
            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("TestPieces", setTestPieces);
            return "events/edit-set-tests";
        }

        // TODO add new set test to database

        return "redirect:/contests/{contestSlug}/" + contestEvent.get().getEventDateForUrl() + "/edit-set-tests";
    }

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-set-tests/{eventPieceId:\\d+}/delete")
    public String removeSetTest(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate, @PathVariable Long eventPieceId) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
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
