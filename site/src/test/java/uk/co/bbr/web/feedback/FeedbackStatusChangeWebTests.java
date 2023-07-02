package uk.co.bbr.web.feedback;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:feedback-list-status-change-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedbackStatusChangeWebTests implements LoginMixin {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CsrfTokenRepository csrfTokenRepository;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_SUPERUSER.getUsername(), TestUser.TEST_SUPERUSER.getPassword(), TestUser.TEST_SUPERUSER.getEmail());
        this.securityService.makeUserSuperuser(TestUser.TEST_SUPERUSER.getUsername());

        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);

        this.securityService.createUser("tjs", "password", "test.1@brassbandresults.co.uk");
        this.securityService.createUser("sms", "password", "test.2@brassbandresults.co.uk");
    }

    @Test
    void testClaimFeedbackWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        testFeedback = this.feedbackService.create(testFeedback);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/claim/sms/" + testFeedback.getId(), String.class);

        // assert
        assertNotNull(response);

        Optional<FeedbackDao> checkFeedback = this.feedbackService.fetchById(testFeedback.getId());
        assertTrue(checkFeedback.isPresent());

        assertEquals("sms", checkFeedback.get().getOwnedBy());
        assertEquals(FeedbackStatus.WITH_USER, checkFeedback.get().getStatus());
        assertEquals("Comment", checkFeedback.get().getComment());
        assertNotEquals(testFeedback.getAuditLog(), checkFeedback.get().getAuditLog());
    }

    @Test
    void testMarkFeedbackDoneWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        testFeedback = this.feedbackService.create(testFeedback);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/done/tjs/" + testFeedback.getId(), String.class);

        // assert
        assertNotNull(response);

        Optional<FeedbackDao> checkFeedback = this.feedbackService.fetchById(testFeedback.getId());
        assertTrue(checkFeedback.isPresent());

        assertEquals("test_user_superuser", checkFeedback.get().getOwnedBy());
        assertEquals(FeedbackStatus.DONE, checkFeedback.get().getStatus());
        assertEquals("Comment", checkFeedback.get().getComment());
        assertNotEquals(testFeedback.getAuditLog(), checkFeedback.get().getAuditLog());
    }

    @Test
    void testSendFeedbackToOwnerWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        testFeedback = this.feedbackService.create(testFeedback);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/owner/tjs/" + testFeedback.getId(), String.class);

        // assert
        assertNotNull(response);

        Optional<FeedbackDao> checkFeedback = this.feedbackService.fetchById(testFeedback.getId());
        assertTrue(checkFeedback.isPresent());

        assertEquals("owner", checkFeedback.get().getOwnedBy());
        assertEquals(FeedbackStatus.OWNER, checkFeedback.get().getStatus());
        assertEquals("Comment", checkFeedback.get().getComment());
        assertNotEquals(testFeedback.getAuditLog(), checkFeedback.get().getAuditLog());
    }

    @Test
    void testCloseFeedbackWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        testFeedback = this.feedbackService.create(testFeedback);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/closed/tjs/" + testFeedback.getId(), String.class);

        // assert
        assertNotNull(response);

        Optional<FeedbackDao> checkFeedback = this.feedbackService.fetchById(testFeedback.getId());
        assertTrue(checkFeedback.isPresent());

        assertEquals("test_user_superuser", checkFeedback.get().getOwnedBy());
        assertEquals(FeedbackStatus.CLOSED, checkFeedback.get().getStatus());
        assertEquals("Comment", checkFeedback.get().getComment());
        assertNotEquals(testFeedback.getAuditLog(), checkFeedback.get().getAuditLog());
    }

    @Test
    void testMarkFeedbackInconclusiveWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        testFeedback = this.feedbackService.create(testFeedback);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/inconclusive/tjs/" + testFeedback.getId(), String.class);

        // assert
        assertNotNull(response);

        Optional<FeedbackDao> checkFeedback = this.feedbackService.fetchById(testFeedback.getId());
        assertTrue(checkFeedback.isPresent());

        assertEquals("test_user_superuser", checkFeedback.get().getOwnedBy());
        assertEquals(FeedbackStatus.INCONCLUSIVE, checkFeedback.get().getStatus());
        assertEquals("Comment", checkFeedback.get().getComment());
        assertNotEquals(testFeedback.getAuditLog(), checkFeedback.get().getAuditLog());
    }

    @Test
    void testMarkFeedbackSpamWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        testFeedback = this.feedbackService.create(testFeedback);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/spam/tjs/" + testFeedback.getId(), String.class);

        // assert
        assertNotNull(response);

        Optional<FeedbackDao> checkFeedback = this.feedbackService.fetchById(testFeedback.getId());
        assertTrue(checkFeedback.isPresent());

        assertEquals("test_user_superuser", checkFeedback.get().getOwnedBy());
        assertEquals(FeedbackStatus.SPAM, checkFeedback.get().getStatus());
        assertEquals("Comment", checkFeedback.get().getComment());
        assertNotEquals(testFeedback.getAuditLog(), checkFeedback.get().getAuditLog());
    }

    @Test
    void testMarkFeedbackWithInvalidTypeFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        final FeedbackDao testFeedbackReturned = this.feedbackService.create(testFeedback);

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/invalid/tjs/" + testFeedbackReturned.getId(), String.class));

        // assert
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }

    @Test
    void testMarkFeedbackWithInvalidFeedbackIdFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        final FeedbackDao testFeedbackReturned = this.feedbackService.create(testFeedback);

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/spam/tjs/999", String.class));

        // assert
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }

    @Test
    void testMarkFeedbackWithInvalidUsernameIdFailsAsExpected() {
        // arrange
        FeedbackDao testFeedback = new FeedbackDao();
        testFeedback.setStatus(FeedbackStatus.NEW);
        testFeedback.setUrl("/test");
        testFeedback.setBrowser("Mozilla/Test");
        testFeedback.setReportedBy("tjs");
        testFeedback.setOwnedBy(null);
        testFeedback.setIp("1.2.3.4");
        testFeedback.setComment("Comment");

        final FeedbackDao testFeedbackReturned = this.feedbackService.create(testFeedback);

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/status-change/spam/not-a-real-user/" + testFeedbackReturned.getId(), String.class));

        // assert
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }

}
