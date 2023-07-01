package uk.co.bbr.web.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=user-lists-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:user-lists-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserListsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_ADMIN.getUsername(), TestUser.TEST_ADMIN.getPassword(), TestUser.TEST_ADMIN.getEmail());
        this.securityService.makeUserAdmin(TestUser.TEST_ADMIN.getUsername());

        loginTestUserByWeb(TestUser.TEST_ADMIN, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupUserData() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.securityService.createUser("member-user", "password1", "member-test@brassbandresults.co.uk");

        this.securityService.createUser("pro-user", "password2", "pro-test@brassbandresults.co.uk");
        this.securityService.makeUserPro("pro-user");

        this.securityService.createUser("superuser-user", "password3", "superuser-test@brassbandresults.co.uk");
        this.securityService.makeUserSuperuser("superuser-user");

        this.securityService.createUser("admin-user", "password4", "admin-test@brassbandresults.co.uk");
        this.securityService.makeUserAdmin("admin-user");

        this.userService.registerNewUser("new-user", "new-test@brassbandresults.co.uk", "password5");

        logoutTestUser();
    }

    @Test
    void testGetUserListReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/user-list", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Users - Brass Band Results</title>"));
        assertTrue(response.contains(">Users<"));

        assertTrue(response.contains("member-user"));
        assertTrue(response.contains("pro-user"));
        assertTrue(response.contains("superuser-user"));
        assertTrue(response.contains("admin-user"));
        assertFalse(response.contains("new-user"));
    }

    @Test
    void testGetProUserListReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/user-list/pro", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Users - Brass Band Results</title>"));
        assertTrue(response.contains(">Users<"));

        assertFalse(response.contains("member-user"));
        assertTrue(response.contains("pro-user"));
        assertFalse(response.contains("superuser-user"));
        assertFalse(response.contains("admin-user"));
        assertFalse(response.contains("new-user"));
    }

    @Test
    void testGetSuperuserUserListReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/user-list/superuser", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Users - Brass Band Results</title>"));
        assertTrue(response.contains(">Users<"));

        assertFalse(response.contains("member-user"));
        assertFalse(response.contains("pro-user"));
        assertTrue(response.contains("superuser-user"));
        assertFalse(response.contains("admin-user"));
        assertFalse(response.contains("new-user"));
    }

    @Test
    void testGetAdminUserListReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/user-list/admin", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Users - Brass Band Results</title>"));
        assertTrue(response.contains(">Users<"));

        assertFalse(response.contains("member-user"));
        assertFalse(response.contains("pro-user"));
        assertFalse(response.contains("superuser-user"));
        assertTrue(response.contains("admin-user"));
        assertFalse(response.contains("new-user"));
    }

    @Test
    void testGetUnactivatedUserListReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/user-list/unactivated", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Users - Brass Band Results</title>"));
        assertTrue(response.contains(">Users<"));

        assertFalse(response.contains("member-user"));
        assertFalse(response.contains("pro-user"));
        assertFalse(response.contains("superuser-user"));
        assertFalse(response.contains("admin-user"));
        assertTrue(response.contains("new-user"));
    }
}
