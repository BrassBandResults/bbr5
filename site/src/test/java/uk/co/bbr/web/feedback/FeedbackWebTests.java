package uk.co.bbr.web.feedback;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
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
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:feedback-feedback-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedbackWebTests implements LoginMixin {

    @Autowired private FeedbackService feedbackService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private SecurityService securityService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @BeforeAll
    void setupUser() {
        this.securityService.createUser("tjs", "password", "test.user@brassbandresults.co.uk");
    }

    @Test
    void testSubmitFeedbackWorkSuccessfully() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());
        headers.add("Referer", "http://localhost:8080/offset/test");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "  http://localhost:8080/offset/test  ");
        map.add("x_owner", "   owner  ");
        map.add("url", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

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

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());
        headers.add("Referer", "http://localhost:8080/url/populated/fail");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "  http://localhost:8080/offset/test  ");
        map.add("x_owner", "   owner  ");
        map.add("url", "something");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/url/populated/fail");
        assertTrue(latestFeedback.isEmpty());
    }

    @Test
    void testSubmitFeedbackWithIncorrectReferrerFailsAsExpected() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());
        headers.add("Referer", "http://blassblandresults.co.uk/incorrect/referer/host");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "  http://localhost:8080/offset/test  ");
        map.add("x_owner", "   owner  ");
        map.add("url", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/incorrect/referer/host");
        assertTrue(latestFeedback.isEmpty());
    }

    @Test
    void testSubmitFeedbackWithNonMatchingReferrerFailsAsExpected() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());
        headers.add("Referer", "http://localhost:8080/incorrect/referer/match");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "http://localhost:8080/offset/test");
        map.add("x_owner", "   owner  ");
        map.add("url", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/incorrect/referer/host");
        assertTrue(latestFeedback.isEmpty());
    }

    @Test
    void testSubmitFeedbackBlankOwnerSucceeds() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());
        headers.add("Referer", "http://localhost:8080/offset/blank-owner");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "http://localhost:8080/offset/blank-owner");
        map.add("x_owner", "");
        map.add("url", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/offset/blank-owner");
        assertFalse(latestFeedback.isEmpty());
    }

    @Test
    void testSubmitFeedbackEmptyOwnerSucceeds() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());
        headers.add("Referer", "http://localhost:8080/offset/empty-owner");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("feedback", "   Some   test  feedback   ");
        map.add("x_url", "http://localhost:8080/offset/empty-owner");
        map.add("x_owner", "    ");
        map.add("url", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/feedback", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<FeedbackDao> latestFeedback = this.feedbackService.fetchLatestFeedback("/offset/empty-owner");
        assertFalse(latestFeedback.isEmpty());
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
