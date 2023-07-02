package uk.co.bbr.web.groups;

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
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
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
        "spring.datasource.url=jdbc:h2:mem:groups-group-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestGroupWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
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
    void setupGroups() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestGroupDao yorkshireGroup = this.contestGroupService.create("Yorkshire Group");
        this.contestGroupService.create("Scotland Group");
        this.contestGroupService.create("AA Group");
        this.contestGroupService.create("AB Group");

        ContestDao yorkshireAreaC = this.contestService.create("Yorkshire Area (Championship Section");
        ContestDao yorkshireArea1 = this.contestService.create("Yorkshire Area (First Section");
        ContestDao yorkshireArea2 = this.contestService.create("Yorkshire Area (Second Section");
        ContestDao yorkshireArea3 = this.contestService.create("Yorkshire Area (Third Section");
        ContestDao yorkshireArea4 = this.contestService.create("Yorkshire Area (Fourth Section");

        yorkshireAreaC = this.contestService.addContestToGroup(yorkshireAreaC, yorkshireGroup);
        yorkshireArea1 = this.contestService.addContestToGroup(yorkshireArea1, yorkshireGroup);
        yorkshireArea2 = this.contestService.addContestToGroup(yorkshireArea2, yorkshireGroup);
        this.contestService.addContestToGroup(yorkshireArea3, yorkshireGroup);

        this.contestEventService.create(yorkshireAreaC, LocalDate.of(2010, 3, 1));
        this.contestEventService.create(yorkshireArea1, LocalDate.of(2010, 3, 2));
        this.contestEventService.create(yorkshireArea2, LocalDate.of(2010, 3, 3));
        this.contestEventService.create(yorkshireArea4, LocalDate.of(2010, 3, 5));

        this.contestEventService.create(yorkshireAreaC, LocalDate.of(2011, 3, 1));
        this.contestEventService.create(yorkshireArea2, LocalDate.of(2011, 3, 1));

        this.contestEventService.create(yorkshireArea4, LocalDate.of(2012, 3, 1));

        logoutTestUser();
    }

    @Test
    void testGetGroupListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contest-groups", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Contest Groups - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Contest Groups starting with A</h2>"));

        assertTrue(response.contains("AA Group"));
        assertTrue(response.contains("AB Group"));
        assertFalse(response.contains("Yorkshire Group"));
        assertFalse(response.contains("Scotland Group"));
    }

    @Test
    void testGetGroupListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contest-groups/Y", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Contest Groups - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Contest Groups starting with Y</h2>"));

        assertFalse(response.contains("AA Group"));
        assertFalse(response.contains("AB Group"));
        assertTrue(response.contains("Yorkshire Group"));
        assertFalse(response.contains("Scotland Group"));

    }

    @Test
    void testGetGroupListForSpecificLetterWithNoResultsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contest-groups/W", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Contest Groups - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Contest Groups starting with W</h2>"));

        assertFalse(response.contains("AA Group"));
        assertFalse(response.contains("AB Group"));
        assertFalse(response.contains("Yorkshire Group"));
        assertFalse(response.contains("Scotland Group"));
    }

    @Test
    void testGetAllGroupsListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contest-groups/ALL", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Contest Groups - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>All Contest Groups</h2>"));

        assertTrue(response.contains("AA Group"));
        assertTrue(response.contains("AB Group"));
        assertTrue(response.contains("Yorkshire Group"));
        assertTrue(response.contains("Scotland Group"));
    }

    @Test
    void testFetchGroupDetailsPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/YORKSHIRE-GROUP", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Group - Group - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Yorkshire Group</h2>"));

        assertTrue(response.contains("Yorkshire Area (Championship Section"));
        assertTrue(response.contains("Yorkshire Area (First Section"));
        assertTrue(response.contains("Yorkshire Area (Second Section"));
        assertTrue(response.contains("Yorkshire Area (Third Section"));
        assertFalse(response.contains("Yorkshire Area (Fourth Section"));
    }

    @Test
    void testFetchGroupWithNoSlugMatchFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/INVALID-GROUP", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testFetchGroupYearsListPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/YORKSHIRE-GROUP/years", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Group - Group - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Yorkshire Group</h2>"));

        assertTrue(response.contains(">2010<"));
        assertTrue(response.contains(">2011<"));
        assertFalse(response.contains(">2012<"));
    }

    @Test
    void testFetchGroupYearsListWithNoSlugMatchFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/INVALID-GROUP/years", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testFetchGroupYearPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/YORKSHIRE-GROUP/2010", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Yorkshire Group in 2010 - Group - Brass Band Results</title>"));
        assertTrue(response.contains(">Yorkshire Group<"));
        assertTrue(response.contains("<span>2010</span>"));

        assertTrue(response.contains("Yorkshire Area (Championship Section"));
        assertTrue(response.contains("Yorkshire Area (First Section"));
        assertTrue(response.contains("Yorkshire Area (Second Section"));
        assertFalse(response.contains("Yorkshire Area (Third Section"));
        assertFalse(response.contains("Yorkshire Area (Fourth Section"));
    }

    @Test
    void testFetchGroupYearWithNoSlugMatchFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/INVALID-GROUP/2010", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
