package uk.co.bbr.web.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-winning-bands-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WinningBandsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
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
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao notRtb = this.bandService.create("Not RTB", yorkshire);
        BandDao whitOnlyBand = this.bandService.create("Whit Band", yorkshire);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
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
        this.contestResultService.addResult(yorkshireArea2001, "1", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2002, "5", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2002, "2", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2003, "3", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2004, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2004, "1", notRtb, duncanBeckley);

        this.contestResultService.addResult(broadoakWhitFriday2010, "3", rtb, davidRoberts);
        this.contestResultService.addResult(broadoakWhitFriday2010, "4", whitOnlyBand, duncanBeckley);

        logoutTestUser();
    }

    @Test
    void testGetWinningBandsPageReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/WINNERS", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Winning Bands - Brass Band Results</title>"));
        assertTrue(response.contains(">Rothwell Temperance Band"));
        assertTrue(response.contains("Not RTB"));
        assertFalse(response.contains("Whit Band"));
    }
}

