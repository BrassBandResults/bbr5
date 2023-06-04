package uk.co.bbr.web.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=tag-member-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:tag-member-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestTagMemberWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private ContestGroupService contestGroupService;
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
    void testDeleteTagWithNoLinksWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestTagService.create("Tag To Delete");
        Optional<ContestTagDao> tagToDelete = this.contestTagService.fetchBySlug("tag-to-delete");
        assertTrue(tagToDelete.isPresent());

        logoutTestUser();

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/tags/tag-to-delete/delete", String.class);

        // assert
        Optional<ContestTagDao> tagToDeleteAfter = this.contestTagService.fetchBySlug("tag-to-delete");
        assertFalse(tagToDeleteAfter.isPresent());
    }

    @Test
    void testDeleteTagWithLinksFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestTagService.create("Tag With Links");
        Optional<ContestTagDao> tagToDelete = this.contestTagService.fetchBySlug("tag-with-links");
        assertTrue(tagToDelete.isPresent());

        Optional<ContestDao> contest = this.contestService.fetchBySlug("yorkshire-area");
        assertTrue(contest.isPresent());

        Optional<ContestGroupDao> contestGroup = this.contestGroupService.fetchBySlug("YORKSHIRE-GROUP");
        assertTrue(contestGroup.isPresent());

        this.contestService.addContestTag(contest.get(), tagToDelete.get());
        this.contestGroupService.addGroupTag(contestGroup.get(), tagToDelete.get());

        logoutTestUser();

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/tags/tag-with-links/delete", String.class));

        // assert
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("400"));
    }
}
