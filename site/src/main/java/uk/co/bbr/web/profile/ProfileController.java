package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.types.ContestHistoryVisibility;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final SecurityService securityService;
    private final PaymentsService paymentsService;
    private final PersonService personService;
    private final PerformanceService performanceService;
    private final FeedbackService feedbackService;

    @IsBbrMember
    @GetMapping("/profile")
    public String profileHome(Model model) {

        SiteUserDao user = this.securityService.getCurrentUser();

        String stripeBuyButtonId = this.paymentsService.fetchStripeBuyButtonId();
        String stripePublishableKey = this.paymentsService.fetchStripePublishableKey();

        model.addAttribute("User", user);
        model.addAttribute("StripeBuyButtonId", stripeBuyButtonId);
        model.addAttribute("StripePublishableKey", stripePublishableKey);

        return "profile/home";
    }

    @IsBbrMember
    @GetMapping("/profile/performances")
    public String profilePerformances(Model model) {
        SiteUserDao user = this.securityService.getCurrentUser();

        List<PerformanceDao> performances = this.performanceService.fetchApprovedPerformancesForUser(user);
        List<PerformanceDao> pendingPerformances = this.performanceService.fetchPendingPerformancesForUser(user);

        Map<String, BandDao> bandList = new HashMap<>();
        Map<String, PersonDao> conductorList = new HashMap<>();

        int winsCount = 0;
        for (PerformanceDao eachPerformance : performances) {
            bandList.put(eachPerformance.getResult().getBand().getSlug(), eachPerformance.getResult().getBand());
            if (eachPerformance.getResult().getConductor() != null) {
                conductorList.put(eachPerformance.getResult().getConductor().getSlug(), eachPerformance.getResult().getConductor());
            }
            if (eachPerformance.getResult().getPosition() != null && eachPerformance.getResult().getPosition() == 1) {
                winsCount++;
            }
        }

        model.addAttribute("User", user);
        model.addAttribute("PendingPerformances", pendingPerformances);
        model.addAttribute("ApprovedPerformances", performances);
        model.addAttribute("ConductorList", conductorList.values());
        model.addAttribute("BandList", bandList.values());
        model.addAttribute("ContestCount", performances.size());
        model.addAttribute("WinsCount", winsCount);

        return "profile/performances";
    }

    @IsBbrMember
    @GetMapping("/profile/performances/make-public")
    public String makePerformancesPublic() {
        SiteUserDao user = this.securityService.getCurrentUser();
        user.setContestHistoryVisibility(ContestHistoryVisibility.PUBLIC);
        this.securityService.update(user);

        return "redirect:/profile/performances";
    }

    @IsBbrMember
    @GetMapping("/profile/performances/make-private")
    public String makePerformancesPrivate() {
        SiteUserDao user = this.securityService.getCurrentUser();
        user.setContestHistoryVisibility(ContestHistoryVisibility.PRIVATE);
        this.securityService.update(user);

        return "redirect:/profile/performances";
    }

    @IsBbrMember
    @GetMapping("/profile/performances/make-site-only")
    public String makePerformancesSiteOnly() {
        SiteUserDao user = this.securityService.getCurrentUser();
        user.setContestHistoryVisibility(ContestHistoryVisibility.SITE_ONLY);
        this.securityService.update(user);

        return "redirect:/profile/performances";
    }

    @IsBbrPro
    @GetMapping("/profile/people-profiles")
    public String profilePersonProfiles(Model model) {
        SiteUserDao user = this.securityService.getCurrentUser();

        List<PersonProfileDao> profiles = this.personService.fetchProfilesForOwner(user.getUsercode());

        model.addAttribute("User", user);
        model.addAttribute("Profiles", profiles);

        return "profile/people-profiles";
    }

    @IsBbrMember
    @GetMapping("/profile/people-profiles")
    public String profileSubmittedFeedback(Model model) {
        SiteUserDao user = this.securityService.getCurrentUser();

        List<FeedbackDao> feedback = this.feedbackService.listForSubmitter(user);

        model.addAttribute("User", user);
        model.addAttribute("Feedback", feedback);

        return "profile/feedback-list";
    }
}

