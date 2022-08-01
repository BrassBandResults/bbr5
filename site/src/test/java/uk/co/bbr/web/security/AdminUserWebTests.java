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
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:security-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username="admin_user", roles= { "BBR_ADMIN" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminUserWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired
    private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_ADMIN.getUsername(), TestUser.TEST_ADMIN.getPassword(), TestUser.TEST_ADMIN.getEmail());
        this.securityService.makeUserAdmin(TestUser.TEST_ADMIN.getUsername());
    }

    @Test
    void testGetRequestUnsecuredPageWithAdminUserSucceeds() {
        loginTestUserByWeb(TestUser.TEST_ADMIN, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/public", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestMemberPageWithAdminUserSucceeds() {
        loginTestUserByWeb(TestUser.TEST_ADMIN, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/member", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestProPageWithAdminUserSucceeds() {
        loginTestUserByWeb(TestUser.TEST_ADMIN, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/pro", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestAdminPageWithAdminUserSucceeds() {
        loginTestUserByWeb(TestUser.TEST_ADMIN, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/admin", String.class);
        assertEquals("user_admin", response);
    }

    @Test
    void testGetRequestSuperuserPageWithAdminUserSucceeds() {
        loginTestUserByWeb(TestUser.TEST_ADMIN, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/superuser", String.class);
        assertEquals("user_admin", response);
    }
}
