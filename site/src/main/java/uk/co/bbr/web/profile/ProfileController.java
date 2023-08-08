package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

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
    @GetMapping("/profile/people-profiles")
    public String profilePersonProfiles(Model model) {
        SiteUserDao user = this.securityService.getCurrentUser();

        List<PersonProfileDao> profiles = this.personService.fetchProfilesForOwner(user.getUsercode());

        model.addAttribute("User", user);
        model.addAttribute("Profiles", profiles);

        return "profile/people-profiles";
    }
}
