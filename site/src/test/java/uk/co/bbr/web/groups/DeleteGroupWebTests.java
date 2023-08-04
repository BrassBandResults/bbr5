package uk.co.bbr.web.groups;

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
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
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
        "spring.datasource.url=jdbc:h2:mem:group-delete-group-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteGroupWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private UserService userService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testDeleteGroupWithNoContestsSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestGroupService.create("Group 1");

        Optional<ContestGroupDao> beforeDelete = this.contestGroupService.fetchBySlug("GROUP-1");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/contest-groups/GROUP-1/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Contest Groups starting with A<"));

        Optional<ContestGroupDao> afterDelete = this.contestGroupService.fetchBySlug("GROUP-1");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteGroupWithAliasesSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestGroupDao group = this.contestGroupService.create("Group 2");
        this.contestGroupService.createAlias(group, "Group 2 Alias");

        Optional<ContestGroupDao> beforeDelete = this.contestGroupService.fetchBySlug("GROUP-2");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/contest-groups/GROUP-2/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Contest Groups starting with A<"));

        Optional<ContestGroupDao> afterDelete = this.contestGroupService.fetchBySlug("GROUP-2");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteGroupWithContestsFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestGroupDao group = this.contestGroupService.create("Group 3");
        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        this.contestService.addContestToGroup(yorkshireArea, group);

        Optional<ContestGroupDao> beforeDelete = this.contestGroupService.fetchBySlug("GROUP-3");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contest-groups/GROUP-3/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This group has contests and cannot be deleted."));

        Optional<ContestGroupDao> afterDelete = this.contestGroupService.fetchBySlug("GROUP-3");
        assertFalse(afterDelete.isEmpty());
    }
}
