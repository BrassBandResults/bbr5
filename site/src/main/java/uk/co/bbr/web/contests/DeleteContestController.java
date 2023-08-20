package uk.co.bbr.web.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DeleteContestController {

    private final ContestService contestService;
    private final ContestEventService contestEventService;

    @IsBbrMember
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/delete")
    public String deleteContest(Model model, @PathVariable("contestSlug") String contestSlug) {

        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestEventDao> futureEventsForContest = this.contestEventService.fetchFutureEventsForContest(contest.get());
        List<ContestEventDao> pastEventsForContest = this.contestEventService.fetchPastEventsForContest(contest.get());

        boolean blocked = !futureEventsForContest.isEmpty() || !pastEventsForContest.isEmpty();

        if (blocked) {
            model.addAttribute("Contest", contest.get());
            model.addAttribute("FutureEvents", futureEventsForContest);
            model.addAttribute("Events", pastEventsForContest);

            return "contests/delete-contest-blocked";
        }

        this.contestService.delete(contest.get());

        return "redirect:/contests";
    }
}
