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
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.events.forms.EventEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EditEventController {

    private final ContestEventService contestEventService;
    private final ContestTypeService contestTypeService;
    private final VenueService venueService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit")
    public String editContestEventForm(Model model, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        EventEditForm editForm = new EventEditForm(contestEvent.get());

        List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();

        model.addAttribute("ContestEvent", contestEvent.get());
        model.addAttribute("Form", editForm);
        model.addAttribute("ContestTypes", contestTypes);

        return "events/edit-event";
    }

    @IsBbrMember
    @PostMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/edit")
    public String editContestEventSave(Model model, @Valid @ModelAttribute("Form") EventEditForm submittedEvent, BindingResult bindingResult, @PathVariable String contestSlug, @PathVariable String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        submittedEvent.validate(bindingResult);

        if (bindingResult.hasErrors()) {
            List<ContestTypeDao> contestTypes = this.contestTypeService.fetchAll();

            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("ContestTypes", contestTypes);
            return "events/edit-event";
        }

        ContestEventDao existingEvent = contestEvent.get();

        existingEvent.setName(submittedEvent.getName());
        existingEvent.setEventDate(submittedEvent.getEventDate());
        existingEvent.setEventDateResolution(ContestEventDateResolution.fromCode(submittedEvent.getDateResolution()));
        existingEvent.setNotes(submittedEvent.getNotes());
        existingEvent.setNoContest(submittedEvent.isNoContest());

        Optional<ContestTypeDao> contestType = this.contestTypeService.fetchById(submittedEvent.getContestType());
        if (contestType.isPresent()) {
            existingEvent.setContestType(contestType.get());
        } else {
            existingEvent.setContestType(null);
        }

        if (submittedEvent.getVenueSlug() != null) {
            Optional<VenueDao> venue = this.venueService.fetchBySlug(submittedEvent.getVenueSlug());
            venue.ifPresent(existingEvent::setVenue);
        } else {
            existingEvent.setVenue(null);
        }

        this.contestEventService.update(existingEvent);

        return "redirect:/contests/{contestSlug}/" + existingEvent.getEventDateForUrl();
    }
}
