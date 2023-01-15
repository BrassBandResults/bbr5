package uk.co.bbr.web;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface LoginMixin {

    default void loginTestUserByWeb(TestUser testUser, RestTemplate restTemplate, CsrfTokenRepository csrfTokenRepository, int port) {
        ResponseEntity<String> response = httpLoginTestUserByWeb(testUser, restTemplate, csrfTokenRepository, port);

        assertEquals(302, response.getStatusCode().value());
        assertNotNull(response.getHeaders().get("Location"));
        assertTrue(response.getHeaders().get("Location").get(0).startsWith("http://localhost:" + port + "/"));
    }

    default void loginTestUser(SecurityService securityService, JwtService jwtService, TestUser testUser) throws AuthenticationFailedException {
        if (!securityService.userExists(testUser.getUsername())) {
            securityService.createUser(testUser.getUsername(), testUser.getPassword(), testUser.getEmail());
        }
        BbrUserDao localUser = securityService.authenticate(testUser.getUsername(), testUser.getPassword());
        String jwtEncoded = jwtService.createJwt(localUser);
        DecodedJWT jwt = jwtService.verifyJwt(jwtEncoded);

        // Token is valid, set auth context
        final Authentication auth = jwtService.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    default void logoutTestUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    default ResponseEntity<String> httpLoginTestUserByWeb(TestUser testUser, RestTemplate restTemplate, CsrfTokenRepository csrfTokenRepository, int port) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("username", testUser.getUsername());
        map.add("password", testUser.getPassword());
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForEntity("http://localhost:" + port + SecurityFilter.URL_SIGN_IN, request, String.class);
    }
}
