package uk.co.bbr.web.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.contests.dto.ContestStreakDto;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestAliasDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.contests.sql.dto.ContestWinsSqlDto;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.web.Tools;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;
    private final ContestEventService contestEventService;
    private final ResultService resultService;

    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}")
    public String contestDetails(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestEventDao> futureEventsForContest = this.contestEventService.fetchFutureEventsForContest(contest.get());
        List<ContestEventDao> pastEventsForContest = this.contestEventService.fetchPastEventsForContest(contest.get());
        int ownChoicePieceCount = this.resultService.fetchCountOfOwnChoiceForContest(contest.get());
        List<ContestAliasDao> contestAliases = this.contestService.fetchAliases(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("FutureEvents", futureEventsForContest);
        model.addAttribute("PastEvents", pastEventsForContest);
        model.addAttribute("OwnChoicePieceCount", ownChoicePieceCount);
        model.addAttribute("PreviousNames", contestAliases);
        model.addAttribute("Description", Tools.markdownToHTML(contest.get().getDescription()));
        model.addAttribute("Notes", Tools.markdownToHTML(contest.get().getNotes()));

        return "contests/contest";
    }

    @IsBbrPro
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/own-choice")
    public String contestOwnChoicePieceDetails(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestResultPieceDao> resultsWithOwnChoicePieces = this.resultService.fetchResultsWithOwnChoicePieces(contest.get());
        int pastEventsCount = this.contestEventService.fetchCountOfEvents(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("OwnChoiceResults", resultsWithOwnChoicePieces);
        model.addAttribute("PastEventsCount", pastEventsCount);
        model.addAttribute("Description", Tools.markdownToHTML(contest.get().getDescription()));
        model.addAttribute("Notes", Tools.markdownToHTML(contest.get().getNotes()));

        return "contests/contest-own-choice";
    }

    @IsBbrPro
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/wins")
    public String contestWins(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestWinsSqlDto> wins = this.resultService.fetchWinsCounts(contest.get());

        int pastEventsCount = this.contestEventService.fetchCountOfEvents(contest.get());
        int ownChoicePieceCount = this.resultService.fetchCountOfOwnChoiceForContest(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("Wins", wins);
        model.addAttribute("PastEventsCount", pastEventsCount);
        model.addAttribute("OwnChoicePieceCount", ownChoicePieceCount);
        model.addAttribute("Description", Tools.markdownToHTML(contest.get().getDescription()));
        model.addAttribute("Notes", Tools.markdownToHTML(contest.get().getNotes()));

        return "contests/contest-wins";
    }

    @IsBbrPro
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/streaks")
    public String contestStreaks(Model model, @PathVariable String contestSlug) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestStreakDto> streaks = this.resultService.fetchStreaksForContest(contest.get());

        int pastEventsCount = this.contestEventService.fetchCountOfEvents(contest.get());
        int ownChoicePieceCount = this.resultService.fetchCountOfOwnChoiceForContest(contest.get());

        model.addAttribute("Contest", contest.get());
        model.addAttribute("Streaks", streaks);
        model.addAttribute("PastEventsCount", pastEventsCount);
        model.addAttribute("OwnChoicePieceCount", ownChoicePieceCount);
        model.addAttribute("Description", Tools.markdownToHTML(contest.get().getDescription()));
        model.addAttribute("Notes", Tools.markdownToHTML(contest.get().getNotes()));

        return "contests/contest-streaks";
    }


    @IsBbrPro
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/position/{position:\\d+|W|D}")
    public String contestResultsForPosition(Model model, @PathVariable String contestSlug, @PathVariable String position) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestResultDao> results = this.resultService.fetchResultsForContestAndPosition(contest.get(), position);

        model.addAttribute("Contest", contest.get());
        model.addAttribute("ResultPosition", position);
        model.addAttribute("Results", results);
        model.addAttribute("Description", Tools.markdownToHTML(contest.get().getDescription()));
        model.addAttribute("Notes", Tools.markdownToHTML(contest.get().getNotes()));

        return "contests/results-for-position";
    }

    @IsBbrPro
    @GetMapping("/contests/{contestSlug:[\\-a-z\\d]{2,}}/draw/{draw:\\d+}")
    public String contestResultsForDraw(Model model, @PathVariable String contestSlug, @PathVariable int draw) {
        Optional<ContestDao> contest = this.contestService.fetchBySlug(contestSlug);

        if (contest.isEmpty()) {
            throw NotFoundException.contestNotFoundBySlug(contestSlug);
        }

        List<ContestResultDao> results = this.resultService.fetchResultsForContestAndDraw(contest.get(), draw);

        model.addAttribute("Contest", contest.get());
        model.addAttribute("DrawPosition", draw);
        model.addAttribute("Results", results);
        model.addAttribute("Description", Tools.markdownToHTML(contest.get().getDescription()));
        model.addAttribute("Notes", Tools.markdownToHTML(contest.get().getNotes()));

        return "contests/results-for-draw";
    }
}
