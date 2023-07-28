package uk.co.bbr.pages.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.sql.dto.ContestListSqlDto;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-list-group-pages-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestListPageContestGroupTests implements LoginMixin {

    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestService contestService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao contest1 = this.contestService.create("Ab Contest 1");
        this.contestService.create("Ac Contest 2");
        ContestDao contest3 = this.contestService.create("Bx Contest 3");
        ContestDao contest4 = this.contestService.create("Bx Contest 4");
        ContestDao contest5 = this.contestService.create("Cx Contest 5");
        ContestDao contest6 = this.contestService.create("Aa Contest 6");

        this.contestService.createAlias(contest1, "Bx Alias 1");
        this.contestService.createAlias(contest5, "Bx Alias 5");
        this.contestService.createAlias(contest6, "Cx Alias 6");
        this.contestService.createAlias(contest6, "Aa Alias 7");
        this.contestService.createAlias(contest6, "Az Alias 8");

        ContestGroupDao group1 = this.contestGroupService.create("Af Group 1");
        this.contestGroupService.create("Bx Group 2");

        contest3.setContestGroup(group1);
        this.contestService.update(contest3);
        contest4.setContestGroup(group1);
        this.contestService.update(contest4);
        contest1.setContestGroup(group1);
        this.contestService.update(contest1);

        logoutTestUser();
    }


    @Test
    void testFetchContestListWithPrefixWorksSuccessfullyForGroups() {
        // act
        List<ContestListSqlDto> pageData = this.contestService.listContestsStartingWith("A");

        // assert
        assertEquals(5, pageData.size());

        assertEquals("Aa Alias 7", pageData.get(0).getName());
        assertEquals("aa-contest-6", pageData.get(0).getSlug());
        assertEquals(0, pageData.get(0).getEventCount());

        assertEquals("Aa Contest 6", pageData.get(1).getName());
        assertEquals("aa-contest-6", pageData.get(1).getSlug());
        assertEquals(0, pageData.get(1).getEventCount());

        assertEquals("Ac Contest 2", pageData.get(2).getName());
        assertEquals("ac-contest-2", pageData.get(2).getSlug());
        assertEquals(0, pageData.get(2).getEventCount());

        assertEquals("Af Group 1", pageData.get(3).getName());
        assertEquals("AF-GROUP-1", pageData.get(3).getSlug());
        assertEquals(0, pageData.get(3).getEventCount());

        assertEquals("Az Alias 8", pageData.get(4).getName());
        assertEquals("aa-contest-6", pageData.get(4).getSlug());
        assertEquals(0, pageData.get(4).getEventCount());
    }

    @Test
    void testFetchContestListLAllWorksSuccessfullyForGroups() {
        // act
        List<ContestListSqlDto> pageData = this.contestService.listContestsStartingWith("ALL");

        // assert
        assertEquals(9, pageData.size());

        assertEquals("Aa Alias 7", pageData.get(0).getName());
        assertEquals("aa-contest-6", pageData.get(0).getSlug());
        assertEquals(0, pageData.get(0).getEventCount());

        assertEquals("Aa Contest 6", pageData.get(1).getName());
        assertEquals("aa-contest-6", pageData.get(1).getSlug());
        assertEquals(0, pageData.get(1).getEventCount());

        assertEquals("Ac Contest 2", pageData.get(2).getName());
        assertEquals("ac-contest-2", pageData.get(2).getSlug());
        assertEquals(0, pageData.get(2).getEventCount());

        assertEquals("Af Group 1", pageData.get(3).getName());
        assertEquals("AF-GROUP-1", pageData.get(3).getSlug());
        assertEquals(0, pageData.get(3).getEventCount());

        assertEquals("Az Alias 8", pageData.get(4).getName());
        assertEquals("aa-contest-6", pageData.get(4).getSlug());
        assertEquals(0, pageData.get(4).getEventCount());

        assertEquals("Bx Alias 5", pageData.get(5).getName());
        assertEquals("cx-contest-5", pageData.get(5).getSlug());
        assertEquals(0, pageData.get(5).getEventCount());

        assertEquals("Bx Group 2", pageData.get(6).getName());
        assertEquals("BX-GROUP-2", pageData.get(6).getSlug());
        assertEquals(0, pageData.get(6).getEventCount());

        assertEquals("Cx Alias 6", pageData.get(7).getName());
        assertEquals("aa-contest-6", pageData.get(7).getSlug());
        assertEquals(0, pageData.get(7).getEventCount());

        assertEquals("Cx Contest 5", pageData.get(8).getName());
        assertEquals("cx-contest-5", pageData.get(8).getSlug());
        assertEquals(0, pageData.get(8).getEventCount());
    }
}



