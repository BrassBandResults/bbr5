package uk.co.bbr.web.contests;

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
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
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
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contest-delete-contest-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteContestWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
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

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 1));

        ContestDao northWestArea = this.contestService.create("North West Area");

        ContestDao LondonArea = this.contestService.create("London Area");
        this.contestService.createAlias(LondonArea, "London & Southern Counties Area");

        logoutTestUser();
    }

    @Test
    void testDeleteContestWithNoEventsSucceeds() {
        Optional<ContestDao> beforeDelete = this.contestService.fetchBySlug("north-west-area");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/contests/north-west-area/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Contests starting with A<"));

        Optional<ContestDao> afterDelete = this.contestService.fetchBySlug("north-west-area");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteContestWithAliasesSucceeds() {
        Optional<ContestDao> beforeDelete = this.contestService.fetchBySlug("london-area");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/contests/london-area/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Contests starting with A<"));

        Optional<ContestDao> afterDelete = this.contestService.fetchBySlug("london-area");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteContestWithEventsFailsAsExpected() {
        Optional<ContestDao> beforeDelete = this.contestService.fetchBySlug("yorkshire-area");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This contest has events and cannot be deleted."));

        Optional<ContestDao> afterDelete = this.contestService.fetchBySlug("yorkshire-area");
        assertFalse(afterDelete.isEmpty());
    }
}
