package uk.co.bbr.web.security;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:security-failed-login-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FailedLoginWebTests implements LoginMixin {

    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @Test
    void testLoginWithInvalidUserFails() {
        ResponseEntity<String> response = httpLoginTestUserByWeb(TestUser.TEST_INVALID, this.restTemplate, this.csrfTokenRepository, this.port);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().indexOf("Sign In") > 0);
    }

    @Test
    void testRequestPageWithInvalidCookieJwtFailsAsExpected() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", SecurityFilter.COOKIE_NAME + "=ThisIsInvalid");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.exchange("http://localhost:" + port + "/test/admin", HttpMethod.GET, requestEntity, String.class));
        assertEquals("403 : \"Invalid user session\"", ex.getMessage());
    }
}
