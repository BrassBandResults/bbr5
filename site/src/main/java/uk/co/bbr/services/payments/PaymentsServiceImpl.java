package uk.co.bbr.services.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.EnvVar;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {

    private final SecurityService securityService;
    private final StripeService stripeService;

    @Override
    public boolean isProAccountActive() {
        BbrUserDao currentUser = this.securityService.getCurrentUser();
        if (currentUser.isProUserForFree()) {
            return true;
        }

        return this.stripeService.isSubscriptionActive(currentUser);
    }

    @Override
    public boolean isProAccountForFree() {
        BbrUserDao currentUser = this.securityService.getCurrentUser();
        return currentUser.isProUserForFree();
    }

    @Override
    public LocalDate getProAccountExpiryDate() {
        BbrUserDao currentUser = this.securityService.getCurrentUser();
        return this.stripeService.subscriptionExpiryDate(currentUser);
    }

    @Override
    public String fetchStripeBuyButtonId() {
        return EnvVar.getEnv("BBR_STRIPE_PUBLIC_BUY_BUTTON", "buy_btn_abc123");
    }

    @Override
    public String fetchStripePublishableKey() {
        return EnvVar.getEnv("BBR_STRIPE_PUBLIC_PUBLISHABLE_KEY", "pk_test_abc123");
    }
}
