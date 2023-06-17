package uk.co.bbr.web.feedback;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=feedback-lists-owner-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:feedback-lists-owner-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeedbackListOwnerWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_ADMIN.getUsername(), TestUser.TEST_ADMIN.getPassword(), TestUser.TEST_ADMIN.getEmail());
        this.securityService.makeUserAdmin(TestUser.TEST_ADMIN.getUsername());

        loginTestUserByWeb(TestUser.TEST_ADMIN, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        FeedbackDao feedbackNew = new FeedbackDao();
        feedbackNew.setStatus(FeedbackStatus.NEW);
        feedbackNew.setComment("  This is a new feedback  ");
        feedbackNew.setBrowser("SquirrelSoft");
        feedbackNew.setIp("1.2.3.4");
        feedbackNew.setUrl("/people");
        this.feedbackService.create(feedbackNew);

        FeedbackDao feedbackOwner = new FeedbackDao();
        feedbackOwner.setStatus(FeedbackStatus.OWNER);
        feedbackOwner.setComment("This is an owner feedback  ");
        feedbackOwner.setBrowser("SquirrelSoft");
        feedbackOwner.setIp("1.2.3.4");
        feedbackOwner.setUrl("/bands");
        this.feedbackService.create(feedbackOwner);

        FeedbackDao feedbackInconclusive = new FeedbackDao();
        feedbackInconclusive.setStatus(FeedbackStatus.INCONCLUSIVE);
        feedbackInconclusive.setComment("  Inconclusive Feedback");
        feedbackInconclusive.setBrowser("SquirrelSoft");
        feedbackInconclusive.setIp("1.2.3.4");
        feedbackInconclusive.setUrl("/pieces");
        this.feedbackService.create(feedbackInconclusive);

        FeedbackDao feedbackSpam = new FeedbackDao();
        feedbackSpam.setStatus(FeedbackStatus.SPAM);
        feedbackSpam.setComment("Spam Feedback   ");
        feedbackSpam.setBrowser("SquirrelSoft");
        feedbackSpam.setIp("1.2.3.4");
        feedbackSpam.setUrl("/spam");
        this.feedbackService.create(feedbackSpam);

        logoutTestUser();
    }

    @Test
    void testFeedbackQueueWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/queue", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Feedback Queue - Brass Band Results</title>"));

        assertTrue(response.contains(">This is a new feedback<"));
        assertTrue(response.contains("a href=\"/people\">/people</a>"));

        assertFalse(response.contains(">This is an owner feedback<"));
        assertFalse(response.contains("a href=\"/bands\">/bands</a>"));

        assertFalse(response.contains(">Inconclusive Feedback<"));
        assertFalse(response.contains("a href=\"/pieces\">/pieces</a>"));

        assertFalse(response.contains(">Spam Feedback<"));
        assertFalse(response.contains("a href=\"/spam\">/spam</a>"));
    }

    @Test
    void testFeedbackOwnerQueueWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/owner", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Feedback Queue - Brass Band Results</title>"));

        assertFalse(response.contains(">This is a new feedback<"));
        assertFalse(response.contains("a href=\"/people\">/people</a>"));

        assertTrue(response.contains(">This is an owner feedback<"));
        assertTrue(response.contains("a href=\"/bands\">/bands</a>"));

        assertFalse(response.contains(">Inconclusive Feedback<"));
        assertFalse(response.contains("a href=\"/pieces\">/pieces</a>"));

        assertFalse(response.contains(">Spam Feedback<"));
        assertFalse(response.contains("a href=\"/spam\">/spam</a>"));
    }

    @Test
    void testFeedbackInconclusiveQueueWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/inconclusive", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Feedback Queue - Brass Band Results</title>"));

        assertFalse(response.contains(">This is a new feedback<"));
        assertFalse(response.contains("a href=\"/people\">/people</a>"));

        assertFalse(response.contains(">This is an owner feedback<"));
        assertFalse(response.contains("a href=\"/bands\">/bands</a>"));

        assertTrue(response.contains(">Inconclusive Feedback<"));
        assertTrue(response.contains("a href=\"/pieces\">/pieces</a>"));

        assertFalse(response.contains(">Spam Feedback<"));
        assertFalse(response.contains("a href=\"/spam\">/spam</a>"));
    }

    @Test
    void testFeedbackSpamQueueWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/spam", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Feedback Queue - Brass Band Results</title>"));

        assertFalse(response.contains(">This is a new feedback<"));
        assertFalse(response.contains("a href=\"/people\">/people</a>"));

        assertFalse(response.contains(">This is an owner feedback<"));
        assertFalse(response.contains("a href=\"/bands\">/bands</a>"));

        assertFalse(response.contains(">Inconclusive Feedback<"));
        assertFalse(response.contains("a href=\"/pieces\">/pieces</a>"));

        assertTrue(response.contains(">Spam Feedback<"));
        assertTrue(response.contains("a href=\"/spam\">/spam</a>"));
    }

    @Test
    void testFeedbackDetailsPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/detail/1", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Feedback Detail - Brass Band Results</title>"));
    }

    @Test
    void testFeedbackDetailsPageWithInvalidIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/feedback/detail/999", String.class));
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));
    }
}
