package uk.co.bbr.web.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-band-details-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandDetailsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private BandRehearsalsService bandRehearsalsService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService resultService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private PersonService personService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao notRtb = this.bandService.create("Not RTB", yorkshire);
        BandDao whitOnlyBand = this.bandService.create("Whit Band", yorkshire);

        this.bandRehearsalsService.createRehearsalDay(rtb, RehearsalDay.MONDAY);
        this.bandRehearsalsService.createRehearsalDay(rtb, RehearsalDay.WEDNESDAY, "After Junior Band");

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestDao yorkshireCup = this.contestService.create("Yorkshire Cup");
        ContestDao broadoakWhitFriday = this.contestService.create("Broadoak (Whit Friday)");

        ContestEventDao yorkshireArea2000 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 1));
        yorkshireArea2000.setEventDateResolution(ContestEventDateResolution.MONTH_AND_YEAR);
        yorkshireArea2000 = this.contestEventService.update(yorkshireArea2000);
        ContestEventDao yorkshireArea2001 = this.contestEventService.create(yorkshireArea, LocalDate.of(2001, 3, 5));
        ContestEventDao yorkshireArea2002 = this.contestEventService.create(yorkshireArea, LocalDate.of(2002, 3, 7));
        ContestEventDao yorkshireArea2003 = this.contestEventService.create(yorkshireArea, LocalDate.of(2003, 3, 10));
        ContestEventDao yorkshireArea2004 = this.contestEventService.create(yorkshireArea, LocalDate.of(2004, 3, 1));
        yorkshireArea2004.setEventDateResolution(ContestEventDateResolution.YEAR);
        yorkshireArea2004 = this.contestEventService.update(yorkshireArea2004);

        ContestEventDao broadoakWhitFriday2010 = this.contestEventService.create(broadoakWhitFriday, LocalDate.of(2010, 5, 1));

        this.resultService.addResult(yorkshireArea2000, "1", rtb, davidRoberts);
        this.resultService.addResult(yorkshireArea2000, "2", notRtb, johnRoberts);

        this.resultService.addResult(yorkshireArea2001, "2", rtb, davidRoberts);
        this.resultService.addResult(yorkshireArea2001, "1", notRtb, johnRoberts);

        this.resultService.addResult(yorkshireArea2002, "5", rtb, johnRoberts);
        this.resultService.addResult(yorkshireArea2002, "2", notRtb, davidRoberts);

        this.resultService.addResult(yorkshireArea2003, "3", notRtb, davidRoberts);

        this.resultService.addResult(yorkshireArea2004, "1", rtb, davidRoberts);
        this.resultService.addResult(yorkshireArea2004, "1", notRtb, duncanBeckley);

        this.resultService.addResult(broadoakWhitFriday2010, "3", rtb, davidRoberts);
        this.resultService.addResult(broadoakWhitFriday2010, "4", whitOnlyBand, duncanBeckley);

        ContestTagDao yorkshireTag = this.contestTagService.create("Yorkshire Tag");
        yorkshireArea = this.contestService.addContestTag(yorkshireArea, yorkshireTag);
        yorkshireCup = this.contestService.addContestTag(yorkshireCup, yorkshireTag);

        ContestTagDao groupTag = this.contestTagService.create("Group Tag");
        ContestGroupDao yorksGroup = this.contestGroupService.create("Yorks");
        yorksGroup = this.contestGroupService.addGroupTag(yorksGroup, groupTag);
        this.contestService.addContestToGroup(yorkshireArea, yorksGroup);


        logoutTestUser();
    }

    @Test
    void testGetBandResultsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Rothwell Temperance Band - Band - Brass Band Results</title>"));
        assertTrue(response.contains("Rothwell Temperance Band"));

        assertTrue(response.contains(">Contests (4)<"));
        assertTrue(response.contains(">Whit Friday (1)<"));

        assertTrue(response.contains(">Yorkshire Area<"));

        assertTrue(response.contains(">2004<"));
        assertFalse(response.contains(">10 Mar 2003<"));
        assertTrue(response.contains(">07 Mar 2002<"));
        assertTrue(response.contains(">05 Mar 2001<"));
        assertTrue(response.contains(">Mar 2000<"));

        assertTrue(response.contains(">David Roberts<"));
        assertTrue(response.contains(">John Roberts<"));
        assertFalse(response.contains(">Duncan Beckley<"));

        assertTrue(response.contains("Monday"));
        assertTrue(response.contains("Wednesday"));
        assertTrue(response.contains("After Junior Band"));
        assertFalse(response.contains("Thursday"));

        assertTrue(response.contains(">Yorkshire Tag<"));
        assertTrue(response.contains(">Group Tag<"));
    }

    @Test
    void testGetBandResultsForInvalidSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetBandNoWhitFridayResultsShowsNoWhitTab() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-rtb", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Not RTB - Band - Brass Band Results</title>"));
        assertTrue(response.contains("Not RTB"));

        assertTrue(response.contains("Contests"));
        assertFalse(response.contains(">Whit Friday"));
    }

    @Test
    void testGetBandWhitsTabRedirectsToResultsListWhenNoWhitResults() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-rtb/whits", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Not RTB - Band - Brass Band Results</title>"));
        assertTrue(response.contains("Not RTB"));

        assertTrue(response.contains(">Contests ("));
        assertFalse(response.contains(">Whit Friday ("));
    }

    @Test
    void testGetBandResultsTabRedirectsToWhitListWhenOnlyWhitResults() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/whit-band", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Whit Band - Band - Brass Band Results</title>"));
        assertTrue(response.contains("Whit Band"));

        assertFalse(response.contains(">Contests ("));
        assertTrue(response.contains(">Whit Friday ("));
    }

    @Test
    void testGetBandWhitResultsForInvalidSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band/whits", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetBandWhitResultsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/whits", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Rothwell Temperance Band - Band - Brass Band Results</title>"));
        assertTrue(response.contains("Rothwell Temperance Band"));

        assertTrue(response.contains(">Contests (4)<"));
        assertTrue(response.contains(">Whit Friday (1)<"));

        assertTrue(response.contains(">Broadoak (Whit Friday)<"));

        assertFalse(response.contains(">5 Nay 2010<"));

        assertTrue(response.contains(">David Roberts<"));
        assertFalse(response.contains(">John Roberts<"));
        assertFalse(response.contains(">Duncan Beckley<"));
    }

    @Test
    void testBandMapReturnsPage() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/MAP", String.class);
        assertNotNull(response);
    }

}

