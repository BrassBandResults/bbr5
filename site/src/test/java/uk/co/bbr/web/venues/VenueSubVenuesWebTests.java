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
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:venue-sub-venues-web-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueSubVenuesWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private VenueService venueService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupVenues() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao parentVenue = this.venueService.create("Parent Venue");

        VenueDao subVenue1 = this.venueService.create("Sub Venue One");
        subVenue1.setParent(parentVenue);
        this.venueService.update(subVenue1);

        VenueDao subVenue2 = this.venueService.create("Sub Venue Two");
        subVenue2.setParent(parentVenue);
        this.venueService.update(subVenue2);

        logoutTestUser();
    }

    @Test
    void testSubVenuesAreDisplayedOnParentVenuePage() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/parent-venue", String.class);
        assertNotNull(response);
        assertTrue(response.contains("Parent Venue"));
        assertTrue(response.contains("Contains"));
        assertTrue(response.contains("Sub Venue One"));
        assertTrue(response.contains("Sub Venue Two"));
    }

    @Test
    void testParentVenueIsDisplayedOnSubVenuePage() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/sub-venue-one", String.class);
        assertNotNull(response);
        assertTrue(response.contains("Sub Venue One"));
        assertTrue(response.contains("Inside"));
        assertTrue(response.contains("Parent Venue"));
    }
}
