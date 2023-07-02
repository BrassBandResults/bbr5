package uk.co.bbr.services.payments;

import uk.co.bbr.services.security.dao.SiteUserDao;

import java.time.LocalDate;

public interface StripeService {
    boolean isSubscriptionActive(SiteUserDao user);

    LocalDate subscriptionExpiryDate(SiteUserDao user);
}
