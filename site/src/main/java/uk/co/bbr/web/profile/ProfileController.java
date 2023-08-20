package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.types.ContestHistoryVisibility;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final SecurityService securityService;
    private final PaymentsService paymentsService;
    private final PersonService personService;
    private final PerformanceService performanceService;

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

        model.addAttribute("User", user);
        model.addAttribute("PendingPerformances", pendingPerformances);
        model.addAttribute("ApprovedPerformances", performances);

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
}
