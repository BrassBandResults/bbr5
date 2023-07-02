package uk.co.bbr.services.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.tags.dto.ContestTagDetailsDto;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-fetch-tag-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FetchContestTagTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private SecurityService securityService;

    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupTags() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestTagDao tag1 = this.contestTagService.create("Tag 1");
        ContestTagDao tag2 = this.contestTagService.create("Tag 2");
        ContestTagDao tag3 = this.contestTagService.create("Tag 3");

        ContestGroupDao group1 = this.contestGroupService.create("Group 1");
        ContestGroupDao group2 = this.contestGroupService.create("Group 2");
        ContestGroupDao group3 = this.contestGroupService.create("Group 3");

        ContestDao contest1 = this.contestService.create("Contest 1");
        ContestDao contest2 = this.contestService.create("Contest 2");
        ContestDao contest3 = this.contestService.create("Contest 3");
        ContestDao contest4 = this.contestService.create("Contest 4");
        ContestDao contest5 = this.contestService.create("Contest 5");

        contest4 = this.contestService.addContestToGroup(contest4, group2);
        contest3 = this.contestService.addContestToGroup(contest3, group2);
        contest2 = this.contestService.addContestToGroup(contest2, group3);
        contest1 = this.contestService.addContestToGroup(contest1, group3);

        group2 = this.contestGroupService.addGroupTag(group2, tag2);
        group2 = this.contestGroupService.addGroupTag(group2, tag1);
        group1 = this.contestGroupService.addGroupTag(group1, tag1);

        contest4 = this.contestService.addContestTag(contest4, tag1);
        contest5 = this.contestService.addContestTag(contest5, tag1);

        logoutTestUser();
    }

    @Test
    void testTagsExistAsCreated() {
        Optional<ContestGroupDao> group1 = this.contestGroupService.fetchBySlug("group-1");
        assertEquals(1, group1.get().getTags().size());

        Optional<ContestGroupDao> group2 = this.contestGroupService.fetchBySlug("group-2");
        assertEquals(2, group2.get().getTags().size());

        Optional<ContestGroupDao> group3 = this.contestGroupService.fetchBySlug("group-3");
        assertEquals(0, group3.get().getTags().size());

        Optional<ContestDao> contest1 = this.contestService.fetchBySlug("contest-1");
        assertEquals(0, contest1.get().getTags().size());

        Optional<ContestDao> contest2 = this.contestService.fetchBySlug("contest-2");
        assertEquals(0, contest2.get().getTags().size());

        Optional<ContestDao> contest3 = this.contestService.fetchBySlug("contest-3");
        assertEquals(0, contest3.get().getTags().size());

        Optional<ContestDao> contest4 = this.contestService.fetchBySlug("contest-4");
        assertEquals(1, contest4.get().getTags().size());

        Optional<ContestDao> contest5 = this.contestService.fetchBySlug("contest-5");
        assertEquals(1, contest5.get().getTags().size());
    }

    @Test
    void testFetchingContestTagWorksAsExpected() {
        // act
        ContestTagDetailsDto contestTagDetails = this.contestTagService.fetchDetailsBySlug("tag-1");

        // assert
        assertEquals(2, contestTagDetails.getContestGroups().size());
        assertEquals("GROUP-1", contestTagDetails.getContestGroups().get(0).getSlug());
        assertEquals("GROUP-2", contestTagDetails.getContestGroups().get(1).getSlug());

        assertEquals(2, contestTagDetails.getContests().size());
        assertEquals("contest-4", contestTagDetails.getContests().get(0).getSlug());
        assertEquals("contest-5", contestTagDetails.getContests().get(1).getSlug());

        assertEquals(4, contestTagDetails.getSortedList().size());
        assertEquals("contest-4", contestTagDetails.getSortedList().get(0).getSlug());
        assertEquals("contest-5", contestTagDetails.getSortedList().get(1).getSlug());
        assertEquals("GROUP-1", contestTagDetails.getSortedList().get(2).getSlug());
        assertEquals("GROUP-2", contestTagDetails.getSortedList().get(3).getSlug());
    }
}



