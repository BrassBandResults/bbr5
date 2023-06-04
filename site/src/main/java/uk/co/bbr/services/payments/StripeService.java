package uk.co.bbr.services.payments;

import uk.co.bbr.services.security.dao.BbrUserDao;

import java.time.LocalDate;

public interface StripeService {
    boolean isSubscriptionActive(BbrUserDao user);

    LocalDate subscriptionExpiryDate(BbrUserDao user);
}
