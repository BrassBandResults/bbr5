package uk.co.bbr.services.payments;

import com.stripe.model.Subscription;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.time.LocalDate;

public interface StripeService {
    boolean isSubscriptionActive(SiteUserDao user);

    LocalDate subscriptionExpiryDate(SiteUserDao user);

    Subscription fetchSubscription(SiteUserDao user);
}
