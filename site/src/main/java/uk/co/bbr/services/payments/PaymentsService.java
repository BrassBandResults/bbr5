package uk.co.bbr.services.payments;

import com.stripe.model.Subscription;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentsService {

    boolean isProAccountActive();

    boolean isProAccountForFree();

    LocalDate getProAccountExpiryDate();

    String fetchStripeBuyButtonId();

    String fetchStripePublishableKey();

    List<SiteUserProDao> markupUsers(List<SiteUserDao> users);

    void recordUpgrade(String usercode, String stripeCheckoutSessionId);

    Optional<Subscription> fetchSubscription(SiteUserDao user);
}
