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
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeleteResultController {

    private final ResultService resultService;
    private final PerformanceService performanceService;
    private final ContestEventService contestEventService;
    private final SecurityService securityService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-_a-z\\d]{2,}}/{contestEventDate:\\d{4}-\\d{2}-\\d{2}}/result/{resultId:\\d+}/delete")
    public String deleteResult(Model model, @PathVariable("contestSlug") String contestSlug, @PathVariable("contestEventDate") String contestEventDate, @PathVariable("resultId") Long resultId) {
        LocalDate eventDate = Tools.parseEventDate(contestEventDate);
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

        if (!performances.isEmpty()) {
            List<ContestResultDao> duplicatesForThisBand = this.resultService.fetchDuplicateResultsForThisBand(result.get());
            if (!duplicatesForThisBand.isEmpty()) {
                for (PerformanceDao performance : performances)
                {
                    performance.setResult(duplicatesForThisBand.get(0));
                    this.performanceService.update(performance);
                }
            } else {

                model.addAttribute("ContestEvent", contestEvent.get());
                model.addAttribute("Result", result.get());
                model.addAttribute("Performances", performances);

                return "events/delete-result-blocked";
            }
        }

        this.resultService.delete(result.get());

        String currentUsername = this.securityService.getCurrentUsername();
        if (result.get().getCreatedBy().equals(currentUsername)) {
            this.securityService.deductOnePoint(currentUsername);
        }

        return "redirect:/contests/{contestSlug}/{contestEventDate}";
    }
}
