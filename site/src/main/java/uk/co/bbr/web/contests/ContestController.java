package uk.co.bbr.web.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.ContestEventService;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultPieceDao;
import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.framework.NotFoundException;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;
    private final ContestEventService contestEventService;
    private final ContestResultService contestResultService;

    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}")
    public String contestDetails(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw new NotFoundException("Contest with slug " + contestSlug + " not found");
        }

        List<ContestEventDao> futureEventsForContest = this.contestEventService.fetchFutureEventsForContest(contest.get());
        List<ContestEventDao> pastEventsForContest = this.contestEventService.fetchPastEventsForContest(contest.get());
        int ownChoicePieceCount = this.contestResultService.fetchCountOfOwnChoiceForContest(contest.get());
        List<ContestAliasDao> contestAliases = this.contestService.fetchAliases(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("FutureEvents", futureEventsForContest);
        model.addAttribute("PastEvents", pastEventsForContest);
        model.addAttribute("OwnChoicePieceCount", ownChoicePieceCount);
        model.addAttribute("PreviousNames", contestAliases);

        return "contests/contests/contest";
    }

    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/own-choice")
    public String contestOwnChoicePieceDetails(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw new NotFoundException("Contest with slug " + contestSlug + " not found");
        }

        List<ContestResultPieceDao> resultsWithOwnChoicePieces = this.contestResultService.fetchResultsWithOwnChoicePieces(contest.get());
        int pastEventsCount = this.contestEventService.fetchCountOfEvents(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("OwnChoiceResults", resultsWithOwnChoicePieces);
        model.addAttribute("PastEventsCount", pastEventsCount);

        return "contests/contests/contest-own-choice";
    }

    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/wins")
    public String contestWins(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw new NotFoundException("Contest with slug " + contestSlug + " not found");
        }

        List<ContestWinsSqlDto> wins = this.contestResultService.fetchWinsCounts(contest.get());

        int pastEventsCount = this.contestEventService.fetchCountOfEvents(contest.get());
        int ownChoicePieceCount = this.contestResultService.fetchCountOfOwnChoiceForContest(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("Wins", wins);
        model.addAttribute("PastEventsCount", pastEventsCount);
        model.addAttribute("OwnChoicePieceCount", ownChoicePieceCount);

        return "contests/contests/contest-wins";
    }
}
