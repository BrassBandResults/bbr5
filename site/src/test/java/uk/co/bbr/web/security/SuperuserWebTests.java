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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-web-tests-superuser-h2", "spring.datasource.url=jdbc:h2:mem:security-web-tests-superuser-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username="admin_user", roles= { "BBR_ADMIN" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuperuserWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_SUPERUSER.getUsername(), TestUser.TEST_SUPERUSER.getPassword(), TestUser.TEST_SUPERUSER.getEmail());
        this.securityService.makeUserSuperuser(TestUser.TEST_SUPERUSER.getUsername());
    }

    @Test
    void testGetRequestUnsecuredPageWithSuperuserSucceeds() {
        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/public", String.class);
        assertEquals(TestUser.TEST_SUPERUSER.getUsername(), response);
    }

    @Test
    void testGetRequestMemberPageWithSuperuserSucceeds(){
        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/member", String.class);
        assertEquals(TestUser.TEST_SUPERUSER.getUsername(), response);
    }

    @Test
    void testGetRequestProPageWithSuperuserSucceeds() {
        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/pro", String.class);
        assertEquals(TestUser.TEST_SUPERUSER.getUsername(), response);
    }

    @Test
    void testGetRequestAdminPageWithSuperuserFails() {
        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + port + "/test/admin", String.class ));
        assertEquals("Forbidden", ex.getStatusCode().getReasonPhrase());
        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void testGetRequestSuperuserPageWithSuperuserFails() {
        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + port + "/test/superuser", String.class);
        assertEquals(TestUser.TEST_SUPERUSER.getUsername(), response);
    }
}
