package uk.co.bbr.services.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.contests.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.contests.dto.ContestTagDetailsDto;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=contest-fetch-group-tests-h2", "spring.datasource.url=jdbc:h2:mem:contest-fetch-group-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FetchContestGroupTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private SecurityService securityService;

    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupGroups() throws AuthenticationFailedException {
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

        contest5.setExtinct(true);
        contest5 = this.contestService.update(contest5);

        contest5 = this.contestService.addContestToGroup(contest5, group2);
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
    void testFetchingContestGroupDetailsWorksAsExpected() {
        // act
        ContestGroupDetailsDto contestGroupDetails = this.contestGroupService.fetchDetailBySlug("group-2");

        // assert
       assertEquals("Group 2", contestGroupDetails.getContestGroup().getName());
       assertEquals("GROUP-2", contestGroupDetails.getContestGroup().getSlug());
       assertEquals(2, contestGroupDetails.getContestGroup().getTags().size());

       assertEquals(2, contestGroupDetails.getActiveContests().size());
        assertEquals("Contest 3", contestGroupDetails.getActiveContests().get(0).getName());
        assertEquals("Contest 4", contestGroupDetails.getActiveContests().get(1).getName());


       assertEquals(1, contestGroupDetails.getOldContests().size());
        assertEquals("Contest 5", contestGroupDetails.getOldContests().get(0).getName());
    }
}



