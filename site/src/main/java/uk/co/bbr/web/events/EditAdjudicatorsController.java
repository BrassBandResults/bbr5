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
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.types.TestPieceAndOr;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.web.events.forms.AddAdjudicatorForm;
import uk.co.bbr.web.events.forms.AddEventSetTestForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditAdjudicatorsController {

    private final PersonService personService;
    private final ContestEventService contestEventService;
    private final ContestTypeService contestTypeService;
    private final PieceService pieceService;
    private final VenueService venueService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-adjudicators")
    public String editAdjudicators(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        AddAdjudicatorForm editForm = new AddAdjudicatorForm();

        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent.get());

        model.addAttribute("ContestEvent", contestEvent.get());
        model.addAttribute("Form", editForm);
        model.addAttribute("Adjudicators", adjudicators);

        return "events/edit-adjudicators";
    }

    @IsBbrMember
    @PostMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-adjudicators")
    public String editAdjudicatorsAdd(Model model, @Valid @ModelAttribute("Form") AddAdjudicatorForm submittedForm, BindingResult bindingResult, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        submittedForm.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent.get());

            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("Adjudicators", adjudicators);
            return "events/edit-adjudicators";
        }

        Optional<PersonDao> adjudicator = this.personService.fetchBySlug(submittedForm.getAdjudicatorSlug());
        if (adjudicator.isEmpty()) {
            List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(contestEvent.get());

            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("Adjudicators", adjudicators);
            return "events/edit-adjudicators";
        }

        this.contestEventService.addAdjudicator(contestEvent.get(), adjudicator.get());

        return "redirect:/contests/{contestSlug}/{contestEventDate}/edit-adjudicators";
    }

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-adjudicators/{adjudicatorId:\\d+}/delete")
    public String removeAdjudicator(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate, @PathVariable Long adjudicatorId) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);
        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        this.contestEventService.removeAdjudicator(contestEvent.get(), adjudicatorId);

        return "redirect:/contests/{contestSlug}/{contestEventDate}/edit-adjudicators";
    }
}
