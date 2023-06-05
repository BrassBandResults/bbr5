package uk.co.bbr.web.venues;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=venue-details-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:venue-details-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueWebTests implements LoginMixin {

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
        this.securityService.createUser(TestUser.TEST_PRO.getUsername(), TestUser.TEST_PRO.getPassword(), TestUser.TEST_PRO.getEmail());
        this.securityService.makeUserPro(TestUser.TEST_PRO.getUsername());

        loginTestUserByWeb(TestUser.TEST_PRO, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_ADMIN);

        VenueDao rah = this.venueService.create("Royal Albert Hall");

        VenueDao birmingham = this.venueService.create("Birmingham");

        VenueDao symfonyHall = this.venueService.create("Symfony Hall");
        symfonyHall.setParent(birmingham);
        symfonyHall = this.venueService.update(symfonyHall);
        VenueAliasDao venueAliasWithDates = new VenueAliasDao();
        venueAliasWithDates.setName("Blackburn Hall");
        venueAliasWithDates.setStartDate(LocalDate.of(1980,5, 4));
        venueAliasWithDates.setEndDate(LocalDate.of(1981, 3, 1));
        this.venueService.createAlias(symfonyHall, venueAliasWithDates);

        VenueAliasDao venueAliasNoDates = new VenueAliasDao();
        venueAliasNoDates.setName("Spanish Hall");
        this.venueService.createAlias(symfonyHall, venueAliasNoDates);

        ContestDao firstSectionFinals = this.contestService.create("National Finals (First Section)");
        ContestDao champSectionFinals = this.contestService.create("National Finals (Championship Section)");

        ContestEventDao firstSection2010 = this.contestEventService.create(firstSectionFinals, LocalDate.of(2010, 8, 1));
        firstSection2010.setVenue(symfonyHall);
        this.contestEventService.update(firstSection2010);

        ContestEventDao champSection2011 = this.contestEventService.create(champSectionFinals, LocalDate.of(2011, 9, 5));
        champSection2011.setVenue(rah);
        this.contestEventService.update(champSection2011);

        logoutTestUser();
    }

    @Test
    void testSinglePageWithAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/symfony-hall", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Symfony Hall - Venue - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Symfony Hall</h2>"));

        assertTrue(response.contains("Also/previously known as"));
        assertTrue(response.contains("Blackburn Hall (1980-1981)"));
        assertTrue(response.contains("Spanish Hall"));

        assertTrue(response.contains(">Inside<"));
        assertTrue(response.contains(">Birmingham<"));

        assertTrue(response.contains("National Finals (First Section)"));
    }

    @Test
    void testSinglePageWithNoSlugMatchFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/invalid-slug", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSingleYearsPageWithAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/symfony-hall/years", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Symfony Hall - Venue - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Symfony Hall</h2>"));

        assertTrue(response.contains("Also/previously known as"));
        assertTrue(response.contains("Blackburn Hall (1980-1981)"));
        assertTrue(response.contains("Spanish Hall"));

        assertTrue(response.contains(">Inside<"));
        assertTrue(response.contains(">Birmingham<"));

        assertTrue(response.contains("2010"));
    }

    @Test
    void testSingleYearsPageWithNoSlugMatchFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/invalid-slug/years", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSingleYearPageWithEventsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/symfony-hall/years/2010", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Symfony Hall in 2010 - Venue - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Symfony Hall</h2>"));
        assertTrue(response.contains("2010<"));

        assertFalse(response.contains("Also/previously known as"));
        assertFalse(response.contains("Blackburn Hall (1980-1981)"));
        assertFalse(response.contains("Spanish Hall"));

        assertFalse(response.contains(">Inside<"));
        assertFalse(response.contains(">Birmingham<"));

        assertTrue(response.contains(">National Finals (First Section)<"));
        assertTrue(response.contains(">01 Aug 2010<"));
    }

    @Test
    void testSingleYearPageWithNoSlugMatchFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/invalid-slug/years/2010", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSingleYearPageWithoutEventsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/symfony-hall/years/2011", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Symfony Hall in 2011 - Venue - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Symfony Hall</h2>"));
        assertTrue(response.contains("2011<"));

        assertFalse(response.contains("Also/previously known as"));
        assertFalse(response.contains("Blackburn Hall (1980-1981)"));
        assertFalse(response.contains("Spanish Hall"));

        assertFalse(response.contains(">Inside<"));
        assertFalse(response.contains(">Birmingham<"));

        assertFalse(response.contains("National Finals"));
    }

    @Test
    void testSinglePageWithoutAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/royal-albert-hall", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Royal Albert Hall - Venue - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Royal Albert Hall</h2>"));

        assertFalse(response.contains("Also/previously known as"));
        assertFalse(response.contains(">Inside<"));
        assertFalse(response.contains(">Birmingham<"));

        assertTrue(response.contains("National Finals (Championship Section)"));
    }

    @Test
    void testSingleYearsPageWithoutAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/royal-albert-hall/years", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Royal Albert Hall - Venue - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Royal Albert Hall</h2>"));

        assertFalse(response.contains("Also/previously known as"));
        assertFalse(response.contains(">Inside<"));
        assertFalse(response.contains(">Birmingham<"));

        assertTrue(response.contains("2011"));
    }

    @Test
    void testContestFilterOnVenueWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/symfony-hall/national-finals-first-section", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Symfony Hall - Venue - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Symfony Hall</h2>"));

        assertFalse(response.contains("Also/previously known as"));
        assertFalse(response.contains(">Inside<"));
        assertFalse(response.contains(">Birmingham<"));

        assertTrue(response.contains("01 Aug 2010"));
        assertFalse(response.contains("2011"));
    }

    @Test
    void testContestFilterOnVenueWithInvalidVenueSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/invalid-slug/national-finals-first-section", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testContestFilterOnVenueWithInvalidContestSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/symfony-hall/invalid-slug", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testVenueMapReturnsPage() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/MAP", String.class);
        assertNotNull(response);
    }

}
