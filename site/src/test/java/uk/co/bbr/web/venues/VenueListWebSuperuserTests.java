package uk.co.bbr.web.venues;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:venues-venue-superuser-list-web-tests-superuser-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueListWebSuperuserTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private VenueService venueService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_SUPERUSER.getUsername(), TestUser.TEST_SUPERUSER.getPassword(), TestUser.TEST_SUPERUSER.getEmail());
        this.securityService.makeUserSuperuser(TestUser.TEST_SUPERUSER.getUsername());

        loginTestUserByWeb(TestUser.TEST_SUPERUSER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao venueWithLocation = this.venueService.create("Auckland");
        venueWithLocation.setLatitude("123.45");
        venueWithLocation.setLongitude("-123.45");
        this.venueService.update(venueWithLocation);

        this.venueService.create("Blackburn Hall");
        this.venueService.create("Symfony Hall");
        this.venueService.create("Royal Albert Hall");
        this.venueService.create("Ballarat Unused");

        VenueDao usedVenue = this.venueService.create("Ballarat Used Venue");
        ContestDao usedContest = this.contestService.create("Used Contest");
        ContestEventDao usedEvent = this.contestEventService.create(usedContest, LocalDate.of(2023, 1, 1));
        usedEvent.setVenue(usedVenue);
        this.contestEventService.update(usedEvent);

        logoutTestUser();
    }

    @Test
    void testGetContestListUnusedWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/UNUSED", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Venues - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Venues starting with UNUSED</h2>"));

        assertTrue(response.contains(">Auckland<"));
        assertTrue(response.contains(">Blackburn Hall<"));
        assertTrue(response.contains(">Symfony Hall<"));
        assertTrue(response.contains(">Royal Albert Hall<"));
        assertTrue(response.contains(">Ballarat Unused<"));
        assertFalse(response.contains(">Ballarat Used Venue<"));
    }

    @Test
    void testGetContestListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/B", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Venues - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Venues starting with B</h2>"));

        assertTrue(response.contains(">Ballarat Unused<"));
        assertTrue(response.contains(">Ballarat Used Venue<"));
    }

    @Test
    void testGetContestListNoLocationWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/NOLOCATION", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Venues - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Venues Without Location</h2>"));

        assertFalse(response.contains(">Auckland<"));
        assertTrue(response.contains(">Blackburn Hall<"));
        assertTrue(response.contains(">Symfony Hall<"));
        assertTrue(response.contains(">Royal Albert Hall<"));
        assertTrue(response.contains(">Ballarat Unused<"));
        assertTrue(response.contains(">Ballarat Used Venue<"));
    }
}
