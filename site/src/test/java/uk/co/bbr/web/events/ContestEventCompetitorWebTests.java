package uk.co.bbr.web.events;

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
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.security.types.ContestHistoryVisibility;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:events-contest-competitor-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestEventCompetitorWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private PerformanceService performanceService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private UserService userService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        this.securityService.createUser(TestUser.TEST_PRO.getUsername(), TestUser.TEST_PRO.getPassword(), TestUser.TEST_PRO.getEmail());
        this.securityService.makeUserPro(TestUser.TEST_PRO.getUsername());

        loginTestUserByWeb(TestUser.TEST_PRO, this.restTemplate, this.csrfTokenRepository, this.port);
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao blackDyke = this.bandService.create("Black Dyke", yorkshire);
        BandDao grimethorpe = this.bandService.create("Grimethorpe", yorkshire);
        BandDao ybs = this.bandService.create("YBS Band", yorkshire);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao duncanBeckley = this.personService.create("Beckley", "Duncan");
        PersonDao davidChilds = this.personService.create("Childs", "David");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 1));
        this.contestResultService.addResult(yorkshireArea2010, "1", blackDyke, davidRoberts);
        this.contestResultService.addResult(yorkshireArea2010, "2", rtb, johnRoberts);
        ContestResultDao result = this.contestResultService.addResult(yorkshireArea2010, "3", grimethorpe, duncanBeckley);
        result.setNotes("Best Principal Cornet Award");
        result = this.contestResultService.update(result);

        Optional<SiteUserDao> proUser = this.userService.fetchUserByUsercode(TestUser.TEST_PRO.getUsername());
        assertTrue(proUser.isPresent());
        proUser.get().setContestHistoryVisibility(ContestHistoryVisibility.PUBLIC);
        this.securityService.update(proUser.get());
        this.performanceService.linkUserPerformance(proUser.get(), result);
    }

    @Test
    void testGetContestEventWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/2010-03-01/competitors", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Area - 01 Mar 2010 - Competitors - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Area<"));
        assertTrue(response.contains("<h3>Mon 01 Mar 2010</h3>"));

        assertFalse(response.contains("Rothwell Temperance Band"));
        assertFalse(response.contains("Black Dyke"));
        assertTrue(response.contains("Grimethorpe"));
        assertFalse(response.contains("YBS Band"));

        assertTrue(response.contains(TestUser.TEST_PRO.getUsername()));
    }
}
