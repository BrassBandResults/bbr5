package uk.co.bbr.web.signup;

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
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.PendingUserDao;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;

import jakarta.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:signup-signup-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SignupWebTests implements LoginMixin {

    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private UserService userService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Test
    void testGetSignupPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/sign-up", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Create Account - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Create Account</h2>"));
        assertTrue(response.contains("a real person and not a computer creating spam accounts, please look at the picture below.<"));
    }

    @Test
    void testGetRegisterPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/register", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Create Account - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Create Account</h2>"));
        assertTrue(response.contains(">Please fill in the details below to create your new account.<"));
    }

    @Test
    void testGetSignupConfirmPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/sign-up-confirm", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Activate Account - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Activate Account</h2>"));
        assertTrue(response.contains(">Your account has been created, but you need to activate it.<"));
    }

    @Test
    void testGetActivatePageWorksSuccessfully() {
        // arrange
        String activationKey = this.userService.registerNewUser("test1", "test1@brassbandresults.co.uk", "password1");

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/activate/" + activationKey, String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>Account Activated - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Account Activated</h2>"));
        assertTrue(response.contains(">Your account has been activated.  Thanks for helping.<"));

        Optional<SiteUserDao> userAfter = this.userService.fetchUserByUsercode("test1");
        assertTrue(userAfter.isPresent());
        assertEquals("test1", userAfter.get().getUsercode());
        assertEquals(UserRole.MEMBER.getCode(), userAfter.get().getAccessLevel());
    }

    @Test
    void testSubmitRegisterPageWorksSuccessfully() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "tjs-test1");
        map.add("email", "tjs-test1@brassbandresults.co.uk");
        map.add("password1", "password1234");
        map.add("password2", "password1234");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/register", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<PendingUserDao> pendingUser = this.userService.fetchPendingUser("tjs-test1");
        assertTrue(pendingUser.isPresent());
        assertEquals("tjs-test1", pendingUser.get().getUsercode());

        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(()-> {
            MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals("tjs-test1@brassbandresults.co.uk", receivedMessage.getAllRecipients()[0].toString());
            assertEquals("BrassBandResults <notification@brassbandresults.co.uk>", receivedMessage.getFrom()[0].toString());
            assertEquals("Account Activation", receivedMessage.getSubject());

            String emailContents = GreenMailUtil.getBody(receivedMessage);

            assertTrue(emailContents.contains("You, or someone using your email address, has signed up for an account on https://brassbandresults.co.uk/"));
            assertTrue(emailContents.contains("To activate your new account, click on the link below."));
            assertTrue(emailContents.contains("/acc/activate/"));
            assertTrue(emailContents.contains("The Brass Band Results Team"));
        });
    }

    @Test
    void testSubmitRegisterPageWithNonMatchingPasswordsFails() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "tjs-test2");
        map.add("email", "tjs-test2@brassbandresults.co.uk");
        map.add("password1", "password1234");
        map.add("password2", "password1235");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/register", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<PendingUserDao> pendingUser = this.userService.fetchPendingUser("tjs-test2");
        assertFalse(pendingUser.isPresent());
    }

    @Test
    void testSubmitRegisterPageWithExistingUsernameFails() {
        // arrange
        String activationKey = this.userService.registerNewUser("tjs-test3", "test@brassbandresults.co.uk", "password1");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "tjs-test3");
        map.add("email", "tjs-test3@brassbandresults.co.uk");
        map.add("password1", "password1234");
        map.add("password2", "password1234");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/register", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSubmitRegisterPageWithShortPasswordFails() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "tjs-test4");
        map.add("email", "tjs-test3@brassbandresults.co.uk");
        map.add("password1", "1234");
        map.add("password2", "1234");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/register", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSubmitAntiSpamPageWithCorrectAnswerWorks() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("section", "bs");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/sign-up", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().contains("Please fill in the details below to create your new account."));
    }

    @Test
    void testSubmitAntiSpamPageWithIncorrectAnswerFails() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("section", "pe");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/sign-up", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("This site is an archive of brass band competition results"));
    }
}
