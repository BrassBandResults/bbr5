package uk.co.bbr.web.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=tag-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:tag-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestTagWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestTagDao aaTag = this.contestTagService.create("AA Tag");
        ContestTagDao abTag = this.contestTagService.create("AB Tag");
        ContestTagDao yorkshireTag = this.contestTagService.create("Yorkshire Tag");
        this.contestTagService.create("North West Tag");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        yorkshireArea = this.contestService.addContestTag(yorkshireArea, aaTag);
        yorkshireArea = this.contestService.addContestTag(yorkshireArea, yorkshireTag);

        ContestDao abbeyHeyContest = this.contestService.create("Abbey Hey Contest");
        this.contestService.addContestTag(abbeyHeyContest, aaTag);

        ContestDao aberdeenContest = this.contestService.create("Aberdeen Contest");
        aberdeenContest = this.contestService.addContestTag(aberdeenContest, aaTag);
        this.contestService.addContestTag(aberdeenContest, abTag);

        ContestDao yorkshireFederationContest = this.contestService.create("Yorkshire Federation Contest");
        yorkshireFederationContest = this.contestService.addContestTag(yorkshireFederationContest, yorkshireTag);

        this.contestService.create("Midlands Area");

        ContestGroupDao yorkshireGroup = this.contestGroupService.create("Yorkshire Group");
        this.contestService.addContestToGroup(yorkshireArea, yorkshireGroup);
        this.contestService.addContestToGroup(yorkshireFederationContest, yorkshireGroup);
        this.contestGroupService.addGroupTag(yorkshireGroup, yorkshireTag);

        logoutTestUser();
    }

    @Test
    void testGetTagListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/tags", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Tags starting with A</h2>"));

        assertTrue(response.contains("AA Tag"));
        assertTrue(response.contains("AB Tag"));
        assertFalse(response.contains("Yorkshire Tag"));
        assertFalse(response.contains("North West Tag"));
    }

    @Test
    void testGetTagListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/tags/Y", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Tags starting with Y</h2>"));

        assertFalse(response.contains("AA Tag"));
        assertFalse(response.contains("AB Tag"));
        assertTrue(response.contains("Yorkshire Tag"));
        assertFalse(response.contains("North West Tag"));

    }

    @Test
    void testGetTagListForSpecificLetterWithNoResultsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/tags/W", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Tags starting with W</h2>"));

        assertFalse(response.contains("AA Tag"));
        assertFalse(response.contains("AB Tag"));
        assertFalse(response.contains("Yorkshire Tag"));
        assertFalse(response.contains("North West Tag"));
    }

    @Test
    void testGetAllTagsListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/tags/ALL", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>All Tags</h2>"));

        assertTrue(response.contains("AA Tag"));
        assertTrue(response.contains("AB Tag"));
        assertTrue(response.contains("Yorkshire Tag"));
        assertTrue(response.contains("North West Tag"));
    }

    @Test
    void testFetchTagDetailsPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/tags/yorkshire-tag", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Yorkshire Tag</h2>"));

        assertTrue(response.contains("Yorkshire Area"));
        assertTrue(response.contains("Yorkshire Group"));
        assertFalse(response.contains("North West Area"));
        assertFalse(response.contains("Aberdeen Contest"));
        assertFalse(response.contains("Abbey Hey Contest"));
    }
}
