package uk.co.bbr.services.payments;

import com.stripe.model.Subscription;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.time.LocalDate;
import java.util.Optional;

public interface StripeService {
    boolean isSubscriptionActive(SiteUserDao user);

    LocalDate subscriptionExpiryDate(SiteUserDao user);

    String fetchEmailFromCheckoutSession(String stripeCheckoutSessionId);

    Optional<Subscription> getActiveSubscription(SiteUserDao user);
}
