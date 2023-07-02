package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final SecurityService securityService;
    private final PaymentsService paymentsService;

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
}
