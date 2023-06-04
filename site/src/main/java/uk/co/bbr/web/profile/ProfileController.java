package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final SecurityService securityService;
    private final PaymentsService paymentsService;

    @GetMapping("/users/{usercode:[a-zA-Z0-9@_\\-.]+}")
    public String profileHome(Model model, @PathVariable("usercode") String usercode) {

        Optional<BbrUserDao> user = this.securityService.fetchUserByUsercode(usercode);
        if (user.isEmpty()) {
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        String stripeBuyButtonId = this.paymentsService.fetchStripeBuyButtonId();
        String stripePublishableKey = this.paymentsService.fetchStripePublishableKey();

        model.addAttribute("User", user.get());
        model.addAttribute("StripeBuyButtonId", stripeBuyButtonId);
        model.addAttribute("StripePublishableKey", stripePublishableKey);

        return "profile/home";
    }
}
