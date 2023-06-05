package uk.co.bbr.web.years;

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
import uk.co.bbr.services.events.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=year-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:year-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class YearWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private ContestService contestService;
    @Autowired private PersonService personService;
    @Autowired private PieceService pieceService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestResultService contestResultService;
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
    void setupData() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao notRtb = this.bandService.create("Not RTB", yorkshire);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");

        PieceDao contestMusic = this.pieceService.create("Contest Music");
        PieceDao fraternity = this.pieceService.create("Fraternity");

        ContestEventDao yorkshireArea2000 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 1));
        yorkshireArea2000.setEventDateResolution(ContestEventDateResolution.MONTH_AND_YEAR);
        yorkshireArea2000 = this.contestEventService.update(yorkshireArea2000);
        ContestEventDao yorkshireArea2001 = this.contestEventService.create(yorkshireArea, LocalDate.of(2001, 3, 5));
        ContestEventDao yorkshireArea2002 = this.contestEventService.create(yorkshireArea, LocalDate.of(2002, 3, 7));
        ContestEventDao yorkshireArea2003 = this.contestEventService.create(yorkshireArea, LocalDate.of(2003, 3, 10));
        ContestEventDao yorkshireArea2004 = this.contestEventService.create(yorkshireArea, LocalDate.of(2004, 3, 1));
        yorkshireArea2004.setEventDateResolution(ContestEventDateResolution.YEAR);
        yorkshireArea2004 = this.contestEventService.update(yorkshireArea2004);
        this.contestEventService.addTestPieceToContest(yorkshireArea2004, contestMusic);

        this.contestResultService.addResult(yorkshireArea2000, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2000, "2", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2001, "2", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2001, "1", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2002, "5", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2002, "2", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2003, "3", notRtb, davidRoberts);

        ContestResultDao result = new ContestResultDao();
        result.setDraw(1);
        result.setPosition("1");
        result.setBand(rtb);
        result.setBandName("Rothwell Temps");
        result.setConductor(davidRoberts);
        result.setConductorSecond(johnRoberts);
        result.setConductorThird(duncanBeckley);

        result = this.contestResultService.addResult(yorkshireArea2004, result);
        this.contestResultService.addPieceToResult(result, fraternity);

        this.contestResultService.addResult(yorkshireArea2004, "1", notRtb, duncanBeckley);

        logoutTestUser();
    }

    @Test
    void testGetYearsPageReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/years", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Years - Brass Band Results</title>"));

        assertFalse(response.contains(">1999<"));
        assertTrue(response.contains(">2000<"));
        assertTrue(response.contains(">2001<"));
        assertTrue(response.contains(">2002<"));
        assertTrue(response.contains(">2003<"));
        assertTrue(response.contains(">2004<"));
        assertFalse(response.contains(">2005<"));
    }

    @Test
    void testSingleYearPageReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/years/2000", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Contests in 2000 - Brass Band Results</title>"));

        assertTrue(response.contains(">Yorkshire Area<"));
    }

    @Test
    void testSingleYearPageWithMultipleConductorsReturnsSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/years/2004", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Contests in 2004 - Brass Band Results</title>"));

        assertTrue(response.contains(">Yorkshire Area<"));
        assertTrue(response.contains(">David Roberts<"));
        assertTrue(response.contains(">John Roberts<"));
        assertTrue(response.contains(">Duncan Beckley<"));
    }
}

