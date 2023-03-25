package uk.co.bbr.web.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestEventService;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-details-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-details-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandDetailsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestResultService contestResultService;
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

        ContestEventDao yorkshireArea2000 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 03, 01));
        yorkshireArea2000.setEventDateResolution(ContestEventDateResolution.MONTH_AND_YEAR);
        yorkshireArea2000 = this.contestEventService.update(yorkshireArea2000);
        ContestEventDao yorkshireArea2001 = this.contestEventService.create(yorkshireArea, LocalDate.of(2001, 03, 05));
        ContestEventDao yorkshireArea2002 = this.contestEventService.create(yorkshireArea, LocalDate.of(2002, 03, 07));
        ContestEventDao yorkshireArea2003 = this.contestEventService.create(yorkshireArea, LocalDate.of(2003, 03, 10));
        ContestEventDao yorkshireArea2004 = this.contestEventService.create(yorkshireArea, LocalDate.of(2004, 03, 1));
        yorkshireArea2004.setEventDateResolution(ContestEventDateResolution.YEAR);
        yorkshireArea2004 = this.contestEventService.update(yorkshireArea2004);

        this.contestResultService.addResult(yorkshireArea2000, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2000, "2", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2001, "2", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2001, "1", notRtb, johnRoberts);

        this.contestResultService.addResult(yorkshireArea2002, "5", rtb, johnRoberts);
        this.contestResultService.addResult(yorkshireArea2002, "2", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2003, "3", notRtb, davidRoberts);

        this.contestResultService.addResult(yorkshireArea2004, "1", rtb, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2004, "1", notRtb, duncanBeckley);

        logoutTestUser();
    }

    @Test
    void testGetBandListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band", String.class);
        assertTrue(response.contains("<h2>Rothwell Temperance Band</h2>"));

        assertTrue(response.contains(">Contests (4)<"));
        assertTrue(response.contains(">Whit Friday (0)<"));

        assertTrue(response.contains(">Yorkshire Area<"));

        assertTrue(response.contains(">2004<"));
        assertFalse(response.contains(">10 Mar 2003<"));
        assertTrue(response.contains(">07 Mar 2002<"));
        assertTrue(response.contains(">05 Mar 2001<"));
        assertTrue(response.contains(">Mar 2000<"));

        assertTrue(response.contains(">David Roberts<"));
        assertTrue(response.contains(">John Roberts<"));
        assertFalse(response.contains(">Duncan Beckley<"));



    }

}

