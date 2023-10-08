package uk.co.bbr.services.payments;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionSearchResult;
import com.stripe.param.SubscriptionSearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.EnvVar;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {

    private final SecurityService securityService;
    private final StripeService stripeService;

    @Override
    public boolean isProAccountActive() {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        if (currentUser.isProUserForFree()) {
            return true;
        }

        return this.stripeService.isSubscriptionActive(currentUser);
    }

    @Override
    public boolean isProAccountForFree() {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        return currentUser.isProUserForFree();
    }

    @Override
    public LocalDate getProAccountExpiryDate() {
        SiteUserDao currentUser = this.securityService.getCurrentUser();
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

    private List<Subscription> getActiveSubscriptions() {
        String stripeKey = EnvVar.getEnv("BBR_STRIPE_PRIVATE_API_KEY", "sk_test_default_abc123");
        if (stripeKey == null || stripeKey.equals("sk_test_default_abc123")) {
            return Collections.emptyList();
        }
        Stripe.apiKey = stripeKey;

        try {
            SubscriptionSearchParams params =
                SubscriptionSearchParams
                    .builder()
                    .setQuery("status:'active'")
                    .setLimit(100L)
                    .addExpand("data.customer")
                    .build();

            SubscriptionSearchResult result = Subscription.search(params);
            return result.getData();
        } catch (StripeException ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<SiteUserProDao> markupUsers(List<SiteUserDao> users) {
        List<Subscription> subscriptions = this.getActiveSubscriptions();

        List<SiteUserProDao> returnData = new ArrayList<>();
        for (SiteUserDao user : users) {
            SiteUserProDao wrappedUser = new SiteUserProDao(user);
            for (Subscription sub : subscriptions) {
                if (user.getStripeEmail() != null && user.getStripeEmail().equals(sub.getCustomerObject().getEmail())) {
                    if (sub.getCurrentPeriodEnd() != null) {
                        wrappedUser.setSubscriptionActive(true);
                        LocalDate endDateTime = Instant.ofEpochSecond(sub.getCurrentPeriodEnd()).atZone(ZoneId.systemDefault()).toLocalDate();
                        wrappedUser.setCurrentSubscriptionEndDate(endDateTime);
                        break;
                    }
                }
            }
            returnData.add(wrappedUser);
        }

        return returnData;
    }

    @Override
    public void recordUpgrade(String usercode, String stripeCheckoutSessionId) {
        return; // TODO
    }
}
