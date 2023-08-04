package uk.co.bbr.web.venues;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:venue-delete-venue-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteVenueWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private VenueService venueService;
    @Autowired private UserService userService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testDeleteVenueWithNoEventsSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.venueService.create("Venue 1");

        Optional<VenueDao> beforeDelete = this.venueService.fetchBySlug("venue-1");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/venues/venue-1/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Venues starting with A<"));

        Optional<VenueDao> afterDelete = this.venueService.fetchBySlug("venue-1");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteVenueWithAliasesSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao venue = this.venueService.create("Venue 2");
        VenueAliasDao previousName = new VenueAliasDao();
        previousName.setName("Old Name");
        this.venueService.createAlias(venue, previousName);

        Optional<VenueDao> beforeDelete = this.venueService.fetchBySlug("venue-2");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/venues/venue-2/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Venues starting with A<"));

        Optional<VenueDao> afterDelete = this.venueService.fetchBySlug("venue-2");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteVenueWithParentLinksSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao venue = this.venueService.create("Venue 3");
        VenueDao parent = this.venueService.create("Parent 3");
        parent.setParent(venue);
        this.venueService.update(parent);

        Optional<VenueDao> beforeDelete = this.venueService.fetchBySlug("venue-3");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/venues/venue-3/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Venues starting with A<"));

        Optional<VenueDao> afterDelete = this.venueService.fetchBySlug("venue-3");
        assertTrue(afterDelete.isEmpty());

        Optional<VenueDao> parentAfterDelete = this.venueService.fetchBySlug("parent-3");
        assertNull(parentAfterDelete.get().getParent());
    }

    @Test
    void testDeleteVenueWithEventsFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao venue = this.venueService.create("Venue 4");
        ContestDao contest = this.contestService.create("Yorkshire Area 4");
        ContestEventDao contestEvent = this.contestEventService.create(contest, LocalDate.of(2013, 3, 1));
        contestEvent.setVenue(venue);
        this.contestEventService.update(contestEvent);


        Optional<VenueDao> beforeDelete = this.venueService.fetchBySlug("venue-4");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/venue-4/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This venue is used for events and cannot be deleted."));

        Optional<VenueDao> afterDelete = this.venueService.fetchBySlug("venue-4");
        assertFalse(afterDelete.isEmpty());
    }
}
