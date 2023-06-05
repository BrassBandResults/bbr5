package uk.co.bbr.web.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.LoginMixin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=profile-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:profile-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfileWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @Test
    void testGetProfileHomeWorksSuccessfully() {
        // arrange
        this.securityService.createUser("tjs", "password", "test@brassbandresults.co.uk");

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/users/tjs", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<title>tjs - User Profile - Brass Band Results</title>"));
    }

    @Test
    void testGetProfileWithNonExistingUserFailsAsExpected() {
        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () ->this.restTemplate.getForObject("http://localhost:" + this.port + "/users/not-a-real-user", String.class));

        // assert
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("404"));
    }

}

