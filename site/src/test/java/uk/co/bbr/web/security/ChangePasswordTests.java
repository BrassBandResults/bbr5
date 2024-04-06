package uk.co.bbr.web.security;

import org.junit.jupiter.api.Assertions;
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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:security-change-password-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChangePasswordTests implements LoginMixin {

    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private UserService userService;
    @Autowired private SecurityService securityService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());

        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testGetChangePasswordPageWorksSuccessfully() {
        // arrange


        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/change-password", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>Change Password - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Change Password</h2>"));
        assertTrue(response.contains("Must be eight characters or longer."));
    }

    @Test
    void testSubmitChangePasswordPageWorksSuccessfully() {
        // arrange
        try {
            this.securityService.authenticate(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword());
        } catch (AuthenticationFailedException e) {
            Assertions.fail("Auth failed before change of password");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("passwordOld", TestUser.TEST_MEMBER.getPassword());
        map.add("passwordNew1", "passwordNew");
        map.add("passwordNew2", "passwordNew");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/change-password", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        try {
            this.securityService.authenticate(TestUser.TEST_MEMBER.getUsername(), "passwordNew");
        } catch (AuthenticationFailedException e) {
            Assertions.fail("Can't login after change of password");
        }

        // put password back what it was
        Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(TestUser.TEST_MEMBER.getUsername());
        this.userService.changePassword(user.get(), TestUser.TEST_MEMBER.getPassword());
    }

    @Test
    void testSubmitChangePasswordPageWithInvalidOldPasswordFailsAsExpected() {        // arrange
        try {
            this.securityService.authenticate(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword());
        } catch (AuthenticationFailedException e) {
            Assertions.fail("Auth failed before change of password");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("passwordOld", "Bob");
        map.add("passwordNew1", "passwordNew");
        map.add("passwordNew2", "passwordNew");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/change-password", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        try {
            this.securityService.authenticate(TestUser.TEST_MEMBER.getUsername(), "passwordNew");
            Assertions.fail("Password change shouldn't have succeeded");
        } catch (AuthenticationFailedException e) {
            // exception expected
        }
    }
    @Test
    void testSubmitChangePasswordPageWithNonMatchingNewPasswordsFailsAsExpected() {        // arrange
        try {
            this.securityService.authenticate(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword());
        } catch (AuthenticationFailedException e) {
            Assertions.fail("Auth failed before change of password");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("passwordOld", TestUser.TEST_MEMBER.getPassword());
        map.add("passwordNew1", "passwordNew");
        map.add("passwordNew2", "passwordDifferent");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/acc/change-password", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        try {
            this.securityService.authenticate(TestUser.TEST_MEMBER.getUsername(), "passwordNew");
            Assertions.fail("Password change shouldn't have succeeded");
        } catch (AuthenticationFailedException e) {
            // exception expected
        }
    }


    @Test
    void testPasswordChangedPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/acc/change-password/changed", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Change Password - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Change Password</h2>"));
        assertTrue(response.contains("Your password has been changed."));
    }
}
