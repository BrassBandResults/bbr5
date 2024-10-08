package uk.co.bbr.web.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeleteEventController {

    private final ResultService resultService;
    private final ContestEventService contestEventService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-_a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/delete")
    public String deleteEvent(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable("contestEventDate") String contestEventDate) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(contestEvent.get());

        if (!eventResults.isEmpty()) {
            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("Results", eventResults);

            return "events/delete-event-blocked";
        }

        this.contestEventService.delete(contestEvent.get());

        return "redirect:/contests/{contestSlug}";
    }
}
