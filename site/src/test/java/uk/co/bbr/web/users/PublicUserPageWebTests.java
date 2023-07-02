package uk.co.bbr.web.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:users-public-user-user-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicUserPageWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

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

        logoutTestUser();
    }

    @Test
    void testGetPublicUserPageWithMemberWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/users/member-user", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>member-user - User Details - Brass Band Results</title>"));
    }

    @Test
    void testGetPublicUserPageWithProUserWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/users/pro-user", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>pro-user - User Details - Brass Band Results</title>"));
    }

    @Test
    void testGetPublicUserPageWithSuperuserWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/users/superuser-user", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>superuser-user - User Details - Brass Band Results</title>"));
    }

    @Test
    void testGetPublicUserPageWithAdminWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/users/admin-user", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>admin-user - User Details - Brass Band Results</title>"));
    }

    @Test
    void testGetPublicUserPageWithNonExistingUserFailsAsExpected() {
        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->this.restTemplate.getForObject("http://localhost:" + this.port + "/users/not-a-real-user", String.class));

        // assert
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("404"));
    }

}

