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
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.dao.UserRole;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface LoginMixin {

    default void loginTestUserByWeb(TestUser testUser, RestTemplate restTemplate, CsrfTokenRepository csrfTokenRepository, int port) {
        ResponseEntity<String> response = httpLoginTestUserByWeb(testUser, restTemplate, csrfTokenRepository, port);

        assertEquals(302, response.getStatusCode().value());
        assertEquals("http://localhost:" + port + "/", response.getHeaders().get("Location").get(0));
    }

    default ResponseEntity<String> httpLoginTestUserByWeb(TestUser testUser, RestTemplate restTemplate, CsrfTokenRepository csrfTokenRepository, int port) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", testUser.getUsername());
        map.add("password", testUser.getPassword());
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + SecurityFilter.URL_SIGN_IN, request, String.class);
        return response;
    }
}
