package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final SecurityService securityService;
    private final PaymentsService paymentsService;

    @IsBbrMember
    @GetMapping("/profile")
    public String profileHome(Model model) {

        BbrUserDao user = this.securityService.getCurrentUser();

        String stripeBuyButtonId = this.paymentsService.fetchStripeBuyButtonId();
        String stripePublishableKey = this.paymentsService.fetchStripePublishableKey();

        model.addAttribute("User", user);
        model.addAttribute("StripeBuyButtonId", stripeBuyButtonId);
        model.addAttribute("StripePublishableKey", stripePublishableKey);

        return "profile/home";
    }
}
