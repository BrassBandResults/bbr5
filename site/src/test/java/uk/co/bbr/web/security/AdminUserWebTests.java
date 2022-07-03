package uk.co.bbr.web.security;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:security-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username="admin_user", roles= { "BBR_ADMIN" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminUserWebTests  implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired
    private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUserConstants.TEST_USER_USERNAME_ADMIN, TestUserConstants.TEST_USER_PASSWORD_ADMIN, TestUserConstants.TEST_USER_EMAIL_ADMIN);
        this.securityService.makeUserAdmin(TestUserConstants.TEST_USER_USERNAME_ADMIN);
    }

    @Test
    void testGetRequestUnsecuredPageWithAdminUserSucceeds() throws AuthenticationFailedException {
        loginAdminByWeb(this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/public", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestMemberPageWithAdminUserSucceeds() throws AuthenticationFailedException {
        loginAdminByWeb(this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/member", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestProPageWithAdminUserSucceeds() throws AuthenticationFailedException {
        loginAdminByWeb(this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/pro", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestAdminPageWithAdminUserSucceeds() throws AuthenticationFailedException {
        loginAdminByWeb(this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/admin", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestSuperuserPageWithAdminUserFails() throws AuthenticationFailedException {
        loginAdminByWeb(this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/superuser", String.class);
        assertEquals("user_admin", response);
    }
}
