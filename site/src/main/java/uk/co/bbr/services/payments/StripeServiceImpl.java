package uk.co.bbr.services.payments;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.EnvVar;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    private Optional<Subscription> getActiveSubscription(SiteUserDao user) {
        Stripe.apiKey = EnvVar.getEnv("BBR_STRIPE_PRIVATE_API_KEY", "sk_test_abc123");

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", user.getStripeCustomer());
            params.put("status", "active");

            SubscriptionCollection subscriptions = Subscription.list(params);
            if (subscriptions.getData().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(subscriptions.getData().get(0));
        } catch (StripeException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Subscription fetchSubscription(SiteUserDao user) {
        Stripe.apiKey = EnvVar.getEnv("BBR_STRIPE_PRIVATE_API_KEY", "sk_test_abc123");

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", user.getStripeCustomer());
            SubscriptionCollection subscriptions = Subscription.list(params);
            if (subscriptions.getData().isEmpty()) {
                return null;
            }
            return subscriptions.getData().get(0);
        } catch (StripeException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isSubscriptionActive(SiteUserDao user) {
        Optional<Subscription> subscription = this.getActiveSubscription(user);
        return subscription.isPresent();
    }

    @Override
    public LocalDate subscriptionExpiryDate(SiteUserDao user) {
        Optional<Subscription> subscription = this.getActiveSubscription(user);
        if (subscription.isEmpty()) {
            return null;
        }
        Long endDateTime = subscription.get().getCurrentPeriodEnd();
        return Instant.ofEpochMilli(endDateTime).atZone(ZoneId.systemDefault()).toLocalDate();
    }


}
