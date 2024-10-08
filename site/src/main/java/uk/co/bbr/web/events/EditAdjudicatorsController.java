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
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.events.forms.AddAdjudicatorForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditAdjudicatorsController {

    private final PersonService personService;
    private final ContestEventService contestEventService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-_a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-adjudicators")
    public String editAdjudicators(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
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
    @PostMapping("/contests/{contestSlug:[\\-_a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-adjudicators")
    public String editAdjudicatorsAdd(Model model, @Valid @ModelAttribute("Form") AddAdjudicatorForm submittedForm, BindingResult bindingResult, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
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
    @GetMapping("/contests/{contestSlug:[\\-_a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit-adjudicators/{adjudicatorId:\\d+}/delete")
    public String removeAdjudicator(@PathVariable String contestSlug, @PathVariable String contestEventDate, @PathVariable Long adjudicatorId) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);
        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        this.contestEventService.removeAdjudicator(contestEvent.get(), adjudicatorId);

        return "redirect:/contests/{contestSlug}/{contestEventDate}/edit-adjudicators";
    }
}
