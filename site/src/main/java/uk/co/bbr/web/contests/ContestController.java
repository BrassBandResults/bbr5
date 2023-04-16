package uk.co.bbr.web.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestEventService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;
    private final ContestEventService contestEventService;

    @GetMapping("/contests")
    public String contestListHome(Model model) {
        return contestListLetter(model, "A");
    }

    @GetMapping("/contests/{letter:[A-Z0-9]{1}}")
    public String contestListLetter(Model model, @PathVariable("letter") String letter) {
        ContestListDto contests = this.contestService.listContestsStartingWith(letter);

        model.addAttribute("ContestPrefixLetter", letter);
        model.addAttribute("Contests", contests);
        return "contests/contests/contests";
    }

    @GetMapping("/contests/ALL")
    public String contestListAll(Model model) {
        ContestListDto contests = this.contestService.listContestsStartingWith("ALL");

        model.addAttribute("ContestPrefixLetter", "ALL");
        model.addAttribute("Contests", contests);
        return "contests/contests/contests";
    }


    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}")
    public String contestDetails(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw new NotFoundException("Contest with slug " + contestSlug + " not found");
        }

        List<ContestEventDao> futureEventsForContest = this.contestEventService.fetchFutureEventsForContest(contest.get());
        List<ContestEventDao> pastEventsForContest = this.contestEventService.fetchPastEventsForContest(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("FutureEvents", futureEventsForContest);
        model.addAttribute("PastEvents", pastEventsForContest);

        return "contests/contests/contest";
    }
}
