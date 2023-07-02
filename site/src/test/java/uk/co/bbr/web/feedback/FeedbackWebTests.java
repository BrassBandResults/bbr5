package uk.co.bbr.web.feedback;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:feedback-feedback-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedbackWebTests implements LoginMixin {

    @Autowired private FeedbackService feedbackService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Test
    void testSubmitFeedbackWorkSuccessfully() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Referer", "http://localhost:8080/offset/test");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "  http://localhost:8080/offset/test  ");
        map.add("x_owner", "   owner  ");
        map.add("url", "");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/feedback/thanks?next=/offset/test"));

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/offset/test");
        assertTrue(latestFeedback.isPresent());
        assertEquals("Some   test  feedback", latestFeedback.get().getComment());

        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(()-> {
            MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals("owner@brassbandresults.co.uk", receivedMessage.getAllRecipients()[0].toString());
            assertEquals("BrassBandResults <notification@brassbandresults.co.uk>", receivedMessage.getFrom()[0].toString());
            assertEquals("Feedback /offset/test", receivedMessage.getSubject());

            String emailContents = GreenMailUtil.getBody(receivedMessage);

            assertTrue(emailContents.contains("Some feedback has been entered on https://www.brassbandresults.co.uk site, on a page containing information that you entered."));
            assertTrue(emailContents.contains("/offset/test"));
            assertTrue(emailContents.contains("Don't want to receive these emails?  Opt out here"));
            assertTrue(emailContents.contains("The Brass Band Results Team"));
        });
    }

    @Test
    void testSubmitFeedbackWithHoneypotPopulatedFailsAsExpected() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Referer", "http://localhost:8080/url/populated/fail");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "  http://localhost:8080/offset/test  ");
        map.add("x_owner", "   owner  ");
        map.add("url", "something");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/feedback/thanks?next=/&t=h"));

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/url/populated/fail");
        assertTrue(latestFeedback.isEmpty());
    }

    @Test
    void testSubmitFeedbackWithIncorrectReferrerFailsAsExpected() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Referer", "http://blassblandresults.co.uk/incorrect/referer/host");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "  http://localhost:8080/offset/test  ");
        map.add("x_owner", "   owner  ");
        map.add("url", "");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/feedback/thanks?next=/&t=r1"));

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/incorrect/referer/host");
        assertTrue(latestFeedback.isEmpty());
    }

    @Test
    void testSubmitFeedbackWithNonMatchingReferrerFailsAsExpected() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Referer", "http://localhost:8080/incorrect/referer/match");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "http://localhost:8080/offset/test");
        map.add("x_owner", "   owner  ");
        map.add("url", "");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/feedback/thanks?next=/&t=r2"));

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/incorrect/referer/host");
        assertTrue(latestFeedback.isEmpty());
    }

    @Test
    void testSubmitThanksWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/thanks?next=/abc/def", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Feedback Thanks - Brass Band Results</title>"));
        assertTrue(response.contains(">Thanks!<"));
        assertTrue(response.contains("Thanks for your feedback. We&#39;ll take a look."));
        assertTrue(response.contains("href=\"/abc/def\""));
    }
}
