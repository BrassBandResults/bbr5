package uk.co.bbr.web.venues;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=venue-details-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:venue-details-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private VenueService venueService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_ADMIN);

        this.venueService.create("Royal Albert Hall");

        VenueDao venue = this.venueService.create("Symfony Hall");
        VenueAliasDao venueAliasWithDates = new VenueAliasDao();
        venueAliasWithDates.setName("Blackburn Hall");
        venueAliasWithDates.setStartDate(LocalDate.of(1980,5, 4));
        venueAliasWithDates.setEndDate(LocalDate.of(1981, 3, 1));
        this.venueService.createAlias(venue, venueAliasWithDates);

        VenueAliasDao venueAliasNoDates = new VenueAliasDao();
        venueAliasNoDates.setName("Spanish Hall");
        this.venueService.createAlias(venue, venueAliasNoDates);

        logoutTestUser();
    }

    @Test
    void testSinglePageWithAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/symfony-hall", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Symfony Hall</h2>"));

        assertTrue(response.contains("Also/previously known as"));
        assertTrue(response.contains("Blackburn Hall (1980-1981)"));
        assertTrue(response.contains("Spanish Hall"));
    }

    @Test
    void testSinglPageWithoutAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/royal-albert-hall", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Royal Albert Hall</h2>"));

        assertFalse(response.contains("Also/previously known as"));
    }

}
