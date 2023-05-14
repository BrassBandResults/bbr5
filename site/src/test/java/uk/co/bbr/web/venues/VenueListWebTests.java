package uk.co.bbr.web.venues;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=venue-list-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:venue-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueListWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private VenueService venueService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.venueService.create("Auckland");
        this.venueService.create("Blackburn Hall");
        this.venueService.create("Symfony Hall");
        this.venueService.create("Royal Albert Hall");
        this.venueService.create("Ballarat");

        logoutTestUser();
    }

    @Test
    void testGetContestListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Venues - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Venues starting with A</h2>"));

        assertTrue(response.contains(">Auckland<"));
        assertFalse(response.contains(">Blackburn Hall<"));
        assertFalse(response.contains(">Symfony Hall<"));
        assertFalse(response.contains(">Royal Albert Hall<"));
        assertFalse(response.contains(">Ballarat<"));
    }

    @Test
    void testGetContestListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/B", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Venues - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Venues starting with B</h2>"));

        assertFalse(response.contains(">Auckland<"));
        assertTrue(response.contains(">Blackburn Hall<"));
        assertFalse(response.contains(">Symfony Hall<"));
        assertFalse(response.contains(">Royal Albert Hall<"));
        assertTrue(response.contains(">Ballarat<"));
    }

    @Test
    void testGetAllContestsListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/ALL", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Venues - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>All Venues</h2>"));

        assertTrue(response.contains(">Auckland<"));
        assertTrue(response.contains(">Blackburn Hall<"));
        assertTrue(response.contains(">Symfony Hall<"));
        assertTrue(response.contains(">Royal Albert Hall<"));
        assertTrue(response.contains(">Ballarat<"));
    }
}
