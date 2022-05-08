package uk.co.bbr.web;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface LoginMixin {

    default void loginAdmin(JwtService jwtService) throws AuthenticationFailedException {
        BbrUserDao user = new BbrUserDao("Administrator", UserRole.ADMIN);
        login(user, jwtService);
    }

    default void loginSuperuser(JwtService jwtService) throws AuthenticationFailedException {
        BbrUserDao user = new BbrUserDao("Superuser", UserRole.SUPERUSER);
        login(user, jwtService);
    }

    default void loginPro(JwtService jwtService) throws AuthenticationFailedException {
        BbrUserDao user = new BbrUserDao("Pro User", UserRole.PRO);
        login(user, jwtService);
    }

    default void loginMember(JwtService jwtService) throws AuthenticationFailedException {
        BbrUserDao user = new BbrUserDao("Member", UserRole.MEMBER);
        login(user, jwtService);
    }

    default void login(BbrUserDao user, JwtService jwtService) {
        String jwtString = jwtService.createJwt(user);
        DecodedJWT jwt = jwtService.verifyJwt(jwtString);

        final Authentication auth = jwtService.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    default void loginAdminByWeb(RestTemplate restTemplate, CsrfTokenRepository csrfTokenRepository, int port) throws AuthenticationFailedException {
        loginByWeb("admin_user", "admin_password", restTemplate, csrfTokenRepository, port);
    }

    default void loginByWeb(String username, String password, RestTemplate restTemplate, CsrfTokenRepository csrfTokenRepository, int port) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", username);
        map.add("password", password);
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + SecurityFilter.URL_SIGN_IN, request, String.class);
        assertEquals(302, response.getStatusCode().value());
        assertEquals("http://localhost:" + port + "/", response.getHeaders().get("Location").get(0));
    }
}
