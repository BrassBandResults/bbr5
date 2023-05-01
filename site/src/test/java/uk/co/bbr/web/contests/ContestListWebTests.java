package uk.co.bbr.web.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=contest-list-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:contest-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestListWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestService.create("Yorkshire Area");
        this.contestService.create("Abbey Hey Contest");
        this.contestService.create("Aberdeen Contest");
        this.contestService.create("Yorkshire Federation Contest");
        this.contestService.create("Midlands Area");

        logoutTestUser();
    }

    @Test
    void testGetContestListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests", String.class);
        assertTrue(response.contains("<h2>Contests starting with A</h2>"));

        assertFalse(response.contains("Yorkshire Area"));
        assertTrue(response.contains("Abbey Hey Contest"));
        assertTrue(response.contains("Aberdeen Contest"));
        assertFalse(response.contains("Yorkshire Federation Contest"));
        assertFalse(response.contains("Midlands Area"));
    }

    @Test
    void testGetContestListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/Y", String.class);
        assertTrue(response.contains("<h2>Contests starting with Y</h2>"));

        assertTrue(response.contains("Yorkshire Area"));
        assertFalse(response.contains("Abbey Hey Contest"));
        assertFalse(response.contains("Aberdeen Contest"));
        assertTrue(response.contains("Yorkshire Federation Contest"));
        assertFalse(response.contains("Midlands Area"));
    }

    @Test
    void testGetAllContestsListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/ALL", String.class);
        assertTrue(response.contains("<h2>All Contests</h2>"));

        assertTrue(response.contains("Yorkshire Area"));
        assertTrue(response.contains("Abbey Hey Contest"));
        assertTrue(response.contains("Aberdeen Contest"));
        assertTrue(response.contains("Yorkshire Federation Contest"));
        assertTrue(response.contains("Midlands Area"));
    }
}
