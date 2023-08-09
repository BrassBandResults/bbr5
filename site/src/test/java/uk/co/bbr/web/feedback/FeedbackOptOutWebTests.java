package uk.co.bbr.web.feedback;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.LoginMixin;

import jakarta.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:feedback-opt-out-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedbackOptOutWebTests implements LoginMixin {

    @Autowired private UserService userService;
    @Autowired private SecurityService securityService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @Test
    void testOptOutFromFeedbackEmailsWorkSuccessfully() {
        // arrange
        SiteUserDao user = this.securityService.createUser("test-user", "password1", "test-user@brassbandresults.co.uk");
        assertFalse(user.isFeedbackEmailOptOut());
        assertNotNull(user.getUuid());

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/feedback/opt-out/" + user.getUuid(), String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>Feedback Email Opt Out - Brass Band Results</title>"));
        assertTrue(response.contains(">Thanks!<"));
        assertTrue(response.contains("opted you out of feedback emails"));

        Optional<SiteUserDao> userAfter = this.userService.fetchUserByUsercode(user.getUsercode());
        assertTrue(userAfter.isPresent());
        assertTrue(userAfter.get().isFeedbackEmailOptOut());
    }

    @Test
    void testOptOutFromFeedbackEmailsWithInvalidUuidFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/feedback/opt-out/not-a-real-user-uuid--------------------", String.class));
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }
}
