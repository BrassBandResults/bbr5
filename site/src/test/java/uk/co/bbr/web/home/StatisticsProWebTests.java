package uk.co.bbr.web.home;

import com.stripe.model.ChargeSearchResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
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
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:home-hone-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatisticsProWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private BandRehearsalsService bandRehearsalsService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
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
        BandDao blackDyke = this.bandService.create("Black Dyke", yorkshire);
        BandDao grimethorpe = this.bandService.create("Grimethorpe", yorkshire);
        BandDao ybs = this.bandService.create("YBS Band", yorkshire);

        this.bandRehearsalsService.createRehearsalDay(rtb, RehearsalDay.MONDAY);
        this.bandRehearsalsService.createRehearsalDay(rtb, RehearsalDay.WEDNESDAY);
        this.bandRehearsalsService.createRehearsalDay(blackDyke, RehearsalDay.MONDAY);
        this.bandRehearsalsService.createRehearsalDay(blackDyke, RehearsalDay.THURSDAY);
        this.bandRehearsalsService.createRehearsalDay(grimethorpe, RehearsalDay.MONDAY);
        this.bandRehearsalsService.createRehearsalDay(grimethorpe, RehearsalDay.TUESDAY);
        this.bandRehearsalsService.createRehearsalDay(ybs, RehearsalDay.TUESDAY);
        this.bandRehearsalsService.createRehearsalDay(ybs, RehearsalDay.SATURDAY);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");
        PersonDao davidChilds = this.personService.create("Childs", "David");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        int lastYear = LocalDate.now().getYear() - 1;
        ContestEventDao yorkshireArea2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(lastYear, 3, 1));
        this.contestResultService.addResult(yorkshireArea2010, "1", blackDyke, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2010, "2", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2010, "3", grimethorpe, duncanBeckley);
        this.contestResultService.addResult(yorkshireArea2010, "", ybs, davidChilds);

        logoutTestUser();
    }

    @Test
    void testGetStatisticsPageReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/statistics", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Statistics - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Statistics</h2>"));
        assertTrue(response.contains("latest is"));
        assertTrue(response.contains("<h3>Band Rehearsals</h3>"));
        assertTrue(response.contains("(75.0%)"));
        assertTrue(response.contains("(50.0%)"));
        assertTrue(response.contains("(25.0%)"));
        assertTrue(response.contains("(0.0%)"));
    }
}
