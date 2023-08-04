package uk.co.bbr.web.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.web.events.forms.ResultEditForm;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeleteResultController {

    private final ResultService resultService;
    private final PerformanceService performanceService;
    private final ContestEventService contestEventService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/result/{resultId:\\d+}/delete")
    public String deleteResult(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable("contestEventDate") String contestEventDate, @PathVariable("resultId") Long resultId) {
        String[] dateSplit = contestEventDate.split("-");
        LocalDate eventDate = LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
        Optional<ContestEventDao> contestEvent = this.contestEventService.fetchEvent(contestSlug, eventDate);

        if (contestEvent.isEmpty()) {
            throw NotFoundException.eventNotFound(contestSlug, contestEventDate);
        }

        Optional <ContestResultDao> result = this.resultService.fetchById(resultId);
        if (result.isEmpty()) {
            throw NotFoundException.resultNotFoundById(resultId);
        }

        if (!result.get().getContestEvent().getId().equals(contestEvent.get().getId())) {
            throw NotFoundException.resultNotOnCorrectContest(contestSlug, contestEventDate);
        }

        List<PerformanceDao> performances = this.performanceService.fetchPerformancesForResult(result.get());

        if (performances.size() > 0) {
            model.addAttribute("ContestEvent", contestEvent.get());
            model.addAttribute("Result", result.get());
            model.addAttribute("Performances", performances);

            return "events/delete-result-blocked";
        }

        this.resultService.delete(result.get());

        return "redirect:/contests/{contestSlug}/{contestEventDate}";
    }
}
