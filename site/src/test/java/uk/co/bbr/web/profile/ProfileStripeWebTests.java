package uk.co.bbr.web.profile;

import com.stripe.model.Subscription;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.payments.StripeService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the profile page for users whose account has a stripe email recorded,
 * where the stripe lookup can return nothing (stale email) or a subscription
 * with no current period end. Both cases previously caused a 500.
 */
@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:profile-stripe-web-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileStripeWebTests implements LoginMixin {

    private static Optional<Subscription> stubbedSubscription = Optional.empty();

    @TestConfiguration
    static class StubStripeConfiguration {
        @Bean
        @Primary
        public StripeService stubStripeService() {
            return new StripeService() {
                @Override
                public boolean isSubscriptionActive(SiteUserDao user) {
                    return stubbedSubscription.isPresent();
                }

                @Override
                public LocalDate subscriptionExpiryDate(SiteUserDao user) {
                    return null;
                }

                @Override
                public String fetchEmailFromCheckoutSession(String stripeCheckoutSessionId) {
                    return null;
                }

                @Override
                public Optional<Subscription> getActiveSubscription(SiteUserDao user) {
                    return stubbedSubscription;
                }
            };
        }
    }

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private UserService userService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() throws AuthenticationFailedException {
        this.securityService.createUser(TestUser.TEST_PRO.getUsername(), TestUser.TEST_PRO.getPassword(), TestUser.TEST_PRO.getEmail());
        this.securityService.makeUserPro(TestUser.TEST_PRO.getUsername());

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_PRO);
        SiteUserDao user = this.userService.fetchUserByUsercode(TestUser.TEST_PRO.getUsername()).get();
        user.setStripeEmail("someone@example.com");
        this.securityService.update(user);
        logoutTestUser();

        loginTestUserByWeb(TestUser.TEST_PRO, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testProfilePageWorksWhenStripeEmailMatchesNoSubscription() {
        // arrange - the stored stripe email no longer matches anything in stripe
        stubbedSubscription = Optional.empty();

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/profile", String.class);

        // assert - page renders with the upgrade option rather than failing
        assertNotNull(response);
        assertTrue(response.contains("User Profile - Brass Band Results</title>"));
    }

    @Test
    void testProfilePageWorksWhenSubscriptionHasNoPeriodEnd() {
        // arrange - an active subscription with no current period end
        Subscription subscription = new Subscription();
        subscription.setCurrentPeriodEnd(null);
        stubbedSubscription = Optional.of(subscription);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/profile", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("User Profile - Brass Band Results</title>"));
    }

    @Test
    void testProfilePageShowsExpiryWhenSubscriptionHasPeriodEnd() {
        // arrange - an active subscription running until well into the future,
        // midday mid-year so the rendered date is 2100 in any server timezone
        Subscription subscription = new Subscription();
        subscription.setCurrentPeriodEnd(4118126400L); // 1 Jul 2100, 12:00 UTC
        stubbedSubscription = Optional.of(subscription);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/profile", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("runs until"));
        assertTrue(response.contains("2100"));
    }
}
