package uk.co.bbr.web.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:events-delete-result-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteResultWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private PerformanceService performanceService;
    @Autowired private UserService userService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(TestUser.TEST_MEMBER.getUsername());
        assertTrue(user.isPresent());

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
        ContestResultDao result1 = this.contestResultService.addResult(yorkshireArea2010, "1", blackDyke, davidRoberts);
        assertEquals(1, result1.getId());
        ContestResultDao result2 = this.contestResultService.addResult(yorkshireArea2010, "2", rtb, johnRoberts);
        assertEquals(2, result2.getId());
        ContestResultDao result3 = this.contestResultService.addResult(yorkshireArea2010, "3", grimethorpe, duncanBeckley);
        assertEquals(3, result3.getId());

        this.performanceService.linkUserPerformance(user.get(), result2);
        this.performanceService.linkUserPerformance(user.get(), result3);

        logoutTestUser();
    }

    @Test
    void testDeleteResultWithNoPerformancesSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        SiteUserDao currentUser = this.securityService.getCurrentUser();
        int pointsBefore = currentUser.getPoints();

        Optional<ContestResultDao> resultBeforeDelete = this.contestResultService.fetchById(1L);
        assertTrue(resultBeforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/contests/yorkshire-area/2010-03-01/result/1/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">I Played At This Contest<"));

        Optional<ContestResultDao> resultAfterDelete = this.contestResultService.fetchById(1L);
        assertTrue(resultAfterDelete.isEmpty());

        currentUser = this.securityService.getCurrentUser();
        assertEquals(pointsBefore == 0 ? 0 : pointsBefore - 1, currentUser.getPoints());
    }

    @Test
    void testDeleteResultWithLinkedUserPerformanceFailsAsExpected() {
        Optional<ContestResultDao> resultBeforeDelete = this.contestResultService.fetchById(2L);
        assertTrue(resultBeforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/2010-03-01/result/2/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This result has linked performances and cannot be deleted."));

        Optional<ContestResultDao> resultAfterDelete = this.contestResultService.fetchById(2L);
        assertFalse(resultAfterDelete.isEmpty());
    }
}
