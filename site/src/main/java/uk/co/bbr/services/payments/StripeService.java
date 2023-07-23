package uk.co.bbr.services.payments;

import com.stripe.model.Subscription;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;

import java.time.LocalDate;

public interface StripeService {
    boolean isSubscriptionActive(SiteUserDao user);

    LocalDate subscriptionExpiryDate(SiteUserDao user);

    SiteUserProDao markupUser(SiteUserDao user);
}
