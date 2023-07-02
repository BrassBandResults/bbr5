package uk.co.bbr.web.security;

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
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.PendingUserDao;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:security-reset-password-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResetPasswordTests implements LoginMixin {

    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private UserService userService;
    @Autowired private SecurityService securityService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser("test-user1", "password1", "test-reset@brassbandresults.co.uk");
        this.securityService.createUser("test-user2", "password2", "test-reset@brassbandresults.co.uk");
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Test
    void testGetResetPasswordPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/forgotten-password", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Forgotten Password - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Forgotten Password</h2>"));
        assertTrue(response.contains("Please enter your username or the email address of your account below."));
    }

    @Test
    void testGetResetPasswordSentPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/forgotten-password/sent", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Forgotten Password - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Forgotten Password</h2>"));
        assertTrue(response.contains("The email associated with your account has been sent a link. Click on this link and follow the instructions to reset your password."));
    }

    @Test
    void testSubmitResetPasswordPageWithUsernameWorksSuccessfully() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("usercode", "  test-user1  ");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/forgotten-password", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/acc/forgotten-password/sent"));

        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(()-> {
            MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals("test-reset@brassbandresults.co.uk", receivedMessage.getAllRecipients()[0].toString());
            assertEquals("BrassBandResults <notification@brassbandresults.co.uk>", receivedMessage.getFrom()[0].toString());
            assertEquals("Password Reset Request", receivedMessage.getSubject());

            String emailContents = GreenMailUtil.getBody(receivedMessage);

            assertTrue(emailContents.contains("Someone has put your username or email into the forgotten password box"));
            assertTrue(emailContents.contains("/acc/forgotten-password/reset/"));
            assertTrue(emailContents.contains("test-user1"));
            assertTrue(emailContents.contains("The Brass Band Results Team"));
        });
    }

    @Test
    void testSubmitResetPasswordPageWithEmailWorksSuccessfully() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("usercode", "  test-reset@brassbandresults.co.uk  ");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/forgotten-password", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/acc/forgotten-password/sent"));

        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals("test-reset@brassbandresults.co.uk", receivedMessage.getAllRecipients()[0].toString());
            assertEquals("BrassBandResults <notification@brassbandresults.co.uk>", receivedMessage.getFrom()[0].toString());
            assertEquals("Password Reset Request", receivedMessage.getSubject());

            String emailContents = GreenMailUtil.getBody(receivedMessage);

            assertTrue(emailContents.contains("Someone has put your username or email into the forgotten password box"));
            assertTrue(emailContents.contains("/acc/forgotten-password/reset/"));
            assertTrue(emailContents.contains("The Brass Band Results Team"));
        });
    }

    @Test
    void testSubmitResetPasswordPageWithInvalidUsernameWorksSuccessfullyWithoutEmail() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("usercode", "  not-a-user  ");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/forgotten-password", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/acc/forgotten-password/sent"));

        Awaitility.await().atMost(2, TimeUnit.SECONDS).untilAsserted(()-> {
            assertEquals(0, greenMail.getReceivedMessages().length);
        });
    }
}
