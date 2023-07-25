package uk.co.bbr.services.payments;

import com.stripe.Stripe;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.SubscriptionSearchResult;
import com.stripe.param.SubscriptionSearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.framework.EnvVar;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;


@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    private Optional<Subscription> getActiveSubscription(SiteUserDao user) {
        Stripe.apiKey = EnvVar.getEnv("BBR_STRIPE_PRIVATE_API_KEY", "sk_test_abc123");

        if (user.getStripeCustomer() == null || user.getStripeCustomer().trim().length() == 0) {
            return Optional.empty();
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", user.getStripeCustomer());

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
