package uk.co.bbr.web.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.statistics.StatisticsService;
import uk.co.bbr.services.statistics.dto.StatisticsDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final SecurityService securityService;
    private final ContestEventService contestEventService;
    private final StatisticsService statisticsService;

    @GetMapping("/")
    public String home(Model model) {

        List<ContestResultDao> lastWeekendsEvents = this.contestEventService.fetchLastWeekend();
        List<ContestResultDao> nextWeekendsEvents = this.contestEventService.fetchNextWeekend();
        List<ContestResultDao> thisWeekendsEvents = this.contestEventService.fetchThisWeekend();

        ContestResultDao todayInHistory = null;
        ContestResultDao thisWeekInHistory = null;

        if (lastWeekendsEvents.isEmpty() && nextWeekendsEvents.isEmpty() && thisWeekendsEvents.isEmpty()) {
            todayInHistory = this.contestEventService.fetchTodayInHistory();
            if (todayInHistory == null) {
               thisWeekInHistory = this.contestEventService.fetchThisWeekInHistory();
            }
        }

        model.addAttribute("LastWeekendEvents", lastWeekendsEvents);
        model.addAttribute("NextWeekendEvents", nextWeekendsEvents);
        model.addAttribute("ThisWeekendEvents", thisWeekendsEvents);
        model.addAttribute("TodayInHistoryEvent", todayInHistory);
        model.addAttribute("ThisWeekInHistoryEvent", thisWeekInHistory);

        return "home/home";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        StatisticsDto statistics = this.statisticsService.fetchStatistics();

        model.addAttribute("Statistics", statistics);
        SiteUserDao currentUser = this.securityService.getCurrentUser();

        if (currentUser != null && currentUser.isProUser()) {
            return "home/statistics-pro";
        }
        return "home/statistics-public";
    }

    @GetMapping("/faq")
    public String faq() {
        return "home/faq";
    }

    @GetMapping("/about-us")
    public String aboutUs() {
        return "home/about-us";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "home/privacy";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {

        List<SiteUserDao> topUsers = this.userService.fetchTopUsers();

        model.addAttribute("TopUsers", topUsers);

        return "home/leaderboard";
    }
}
