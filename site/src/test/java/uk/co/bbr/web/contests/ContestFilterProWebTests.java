package uk.co.bbr.web.contests;

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
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
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

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-filter-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestFilterProWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private BandRehearsalsService bandRehearsalsService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private PersonService personService;
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

        this.contestResultService.addResult(yorkshireArea2000, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2000, "2", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2001, "2", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2001, "1", notRtb, duncanBeckley);

        ContestResultDao drawResult = this.contestResultService.addResult(yorkshireArea2002, "5", rtb, johnRoberts);
        drawResult.setDraw(1);
        this.contestResultService.update(drawResult);
        this.contestResultService.addResult(yorkshireArea2002, "2", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2003, "3", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2004, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2004, "1", notRtb, duncanBeckley);

        this.contestResultService.addResult(broadoakWhitFriday2010, "3", rtb, davidRoberts);
        this.contestResultService.addResult(broadoakWhitFriday2010, "4", whitOnlyBand, duncanBeckley);

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
    void testGetContestOwnChoicePageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/own-choice", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Area - Contest - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Area<"));
    }

    @Test
    void testGetContestOwnChoicePageWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-contest-slug/own-choice", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetContestWinsPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/wins", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Area - Contest Wins - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Area<"));
    }

    @Test
    void testGetContestWinsPageWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-contest-slug/wins", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetContestStreaksPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/streaks", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Area - Contest Streaks - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Area<"));
    }

    @Test
    void testGetContestStreaksPageWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-contest-slug/streaks", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetContestFilterToDrawPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/draw/1", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Area - For Draw - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Area<"));
        assertTrue(response.contains(">Rothwell Temperance Band<"));
        assertTrue(response.contains("John Roberts"));
    }

    @Test
    void testGetContestFilterToDrawPageWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-contest-slug/draw/1", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetContestFilterToPositionPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/position/1", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Area - For Result - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Area<"));
        assertTrue(response.contains(">Rothwell Temperance Band<"));
        assertFalse(response.contains("John Roberts"));
    }

    @Test
    void testGetContestFilterToPositionPageWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-contest-slug/position/1", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }






}
