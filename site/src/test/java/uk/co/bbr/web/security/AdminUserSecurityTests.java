package uk.co.bbr.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-controller-tests-h2", "spring.datasource.url=jdbc:h2:mem:security-controller-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username="admin_user", roles= { "BBR_ADMIN" })
class AdminUserSecurityTests implements LoginMixin {

    @Autowired private JwtService jwtService;
    @Autowired private SecurityService securityService;

    @Autowired private TestSecurityController securityTestController;

    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;



    @Test
    void testAccessUnsecuredPageWithAdminUserSucceeds() {
        String userName = securityTestController.testPublic(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessSecuredPageWithAdminUserSucceeds() {
        String userName = securityTestController.testMember(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);

    }

    @Test
    void testAccessProAccountPageWithAdminUserSucceeds() {
        String userName = securityTestController.testPro(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);

    }

    @Test
    void testAccessSuperuserPageWithAdminUserSucceeds() {
        String userName = securityTestController.testSuperuser(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessAdminPageWithAdminUserSucceeds() {
        String userName = securityTestController.testAdmin(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testGetRequestUnsecuredPageWithAdminUserSucceeds() throws AuthenticationFailedException {
        loginAdminByWeb(this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/public",String.class);
        assertEquals("admin_user", response);
    }

}
