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
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentsServiceImpl implements PaymentsService {

    private final SecurityService securityService;
    private final StripeService stripeService;
    private final UserService userService;

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
            List<Subscription> subs = new ArrayList<>(result.getData());

            if (subs.size() == 100) {
                SubscriptionSearchParams params2 =
                    SubscriptionSearchParams
                        .builder()
                        .setQuery("status:'active'")
                        .setLimit(100L)
                        .setPage(result.getNextPage())
                        .addExpand("data.customer")
                        .build();

                SubscriptionSearchResult result2 = Subscription.search(params2);
                subs.addAll(result2.getData());
                return subs;
            }
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
                if (user.getStripeEmail() != null && user.getStripeEmail().equalsIgnoreCase(sub.getCustomerObject().getEmail())) {
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
        try {
            String stripeEmail = this.stripeService.fetchEmailFromCheckoutSession(stripeCheckoutSessionId);

            Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(usercode);
            if (user.isPresent()) {
                user.get().setStripeEmail(stripeEmail);
                this.securityService.update(user.get());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
           // do nothing
        }
    }

    @Override
    public Optional<Subscription> fetchSubscription(SiteUserDao user) {
        if (user.getStripeEmail() != null) {
            Optional<Subscription> optSub = this.stripeService.getActiveSubscription(user);
            return optSub;
        }
        return Optional.empty();
    }
}
