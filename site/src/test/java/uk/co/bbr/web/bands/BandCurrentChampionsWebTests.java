package uk.co.bbr.web.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-band-current-champions-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandCurrentChampionsWebTests implements LoginMixin {

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

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestDao yorkshireCup = this.contestService.create("Yorkshire Cup");
        ContestDao oldResult = this.contestService.create("Old Contest");

        LocalDate agesAgo = LocalDate.now().minus(2, ChronoUnit.YEARS);
        LocalDate lastMonth = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        LocalDate today = LocalDate.now();

        ContestEventDao yorkshireAreaLastMonth = this.contestEventService.create(yorkshireArea, lastMonth);
        ContestEventDao yorkshireAreaToday = this.contestEventService.create(yorkshireArea, today);
        ContestEventDao oldContest = this.contestEventService.create(oldResult, agesAgo);
        ContestEventDao yorkshireCupLastMonth = this.contestEventService.create(yorkshireCup, lastMonth);

        this.resultService.addResult(yorkshireAreaLastMonth, "1", rtb, davidRoberts);
        this.resultService.addResult(yorkshireAreaLastMonth, "2", notRtb, johnRoberts);

        this.resultService.addResult(yorkshireAreaToday, "1", notRtb, johnRoberts);

        this.resultService.addResult(oldContest, "1", rtb, davidRoberts);
        this.resultService.addResult(oldContest, "2", notRtb, johnRoberts);

        this.resultService.addResult(yorkshireCupLastMonth, "1", rtb, davidRoberts);
        this.resultService.addResult(yorkshireCupLastMonth, "2", notRtb, johnRoberts);

        logoutTestUser();
    }

    @Test
    void testGetBandResultsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Rothwell Temperance Band - Band - Brass Band Results</title>"));
        assertTrue(response.contains("Rothwell Temperance Band"));

        assertTrue(response.contains("Current"));
        assertTrue(response.contains("Champions"));
        assertTrue(response.contains("Yorkshire Cup"));
    }
}

