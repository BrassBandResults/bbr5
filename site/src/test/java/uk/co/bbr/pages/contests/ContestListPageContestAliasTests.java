package uk.co.bbr.pages.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-list-aliases-pages-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestListPageContestAliasTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao contest1 = this.contestService.create("Ab Contest 1");
        ContestDao contest2 = this.contestService.create("Ac Contest 2");
        ContestDao contest3 = this.contestService.create("Bx Contest 3");
        ContestDao contest4 = this.contestService.create("Bx Contest 4");
        ContestDao contest5 = this.contestService.create("Cx Contest 5");
        ContestDao contest6 = this.contestService.create("Aa Contest 6");

        this.contestService.createAlias(contest1, "Bx Alias 1");
        this.contestService.createAlias(contest5, "Bx Alias 5");
        this.contestService.createAlias(contest6, "Cx Alias 6");
        this.contestService.createAlias(contest6, "Aa Alias 7");
        this.contestService.createAlias(contest6, "Az Alias 8");

        logoutTestUser();
    }


    @Test
    void testFetchContestListWithPrefixWorksSuccessfullyForContestAliases() {
        // act
        ContestListDto pageData = this.contestService.listContestsStartingWith("A");

        // assert
        assertEquals("A", pageData.getSearchPrefix());
        assertEquals(5, pageData.getReturnedContests().size());

        assertEquals("Aa Alias 7", pageData.getReturnedContests().get(0).getName());
        assertEquals("aa-contest-6", pageData.getReturnedContests().get(0).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(0).getContestResultsCount());

        assertEquals("Aa Contest 6", pageData.getReturnedContests().get(1).getName());
        assertEquals("aa-contest-6", pageData.getReturnedContests().get(1).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(1).getContestResultsCount());

        assertEquals("Ab Contest 1", pageData.getReturnedContests().get(2).getName());
        assertEquals("ab-contest-1", pageData.getReturnedContests().get(2).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(2).getContestResultsCount());

        assertEquals("Ac Contest 2", pageData.getReturnedContests().get(3).getName());
        assertEquals("ac-contest-2", pageData.getReturnedContests().get(3).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(3).getContestResultsCount());

        assertEquals("Az Alias 8", pageData.getReturnedContests().get(4).getName());
        assertEquals("aa-contest-6", pageData.getReturnedContests().get(4).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(4).getContestResultsCount());
    }

    @Test
    void testFetchContestListLAllWorksSuccessfullyForContestAliases() {
        // act
        ContestListDto pageData = this.contestService.listContestsStartingWith("ALL");

        // assert
        assertEquals("ALL", pageData.getSearchPrefix());
        assertEquals(11, pageData.getReturnedContests().size());

        assertEquals("Aa Alias 7", pageData.getReturnedContests().get(0).getName());
        assertEquals("aa-contest-6", pageData.getReturnedContests().get(0).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(0).getContestResultsCount());

        assertEquals("Aa Contest 6", pageData.getReturnedContests().get(1).getName());
        assertEquals("aa-contest-6", pageData.getReturnedContests().get(1).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(1).getContestResultsCount());

        assertEquals("Ab Contest 1", pageData.getReturnedContests().get(2).getName());
        assertEquals("ab-contest-1", pageData.getReturnedContests().get(2).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(2).getContestResultsCount());

        assertEquals("Ac Contest 2", pageData.getReturnedContests().get(3).getName());
        assertEquals("ac-contest-2", pageData.getReturnedContests().get(3).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(3).getContestResultsCount());

        assertEquals("Az Alias 8", pageData.getReturnedContests().get(4).getName());
        assertEquals("aa-contest-6", pageData.getReturnedContests().get(4).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(4).getContestResultsCount());

        assertEquals("Bx Alias 1", pageData.getReturnedContests().get(5).getName());
        assertEquals("ab-contest-1", pageData.getReturnedContests().get(5).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(5).getContestResultsCount());

        assertEquals("Bx Alias 5", pageData.getReturnedContests().get(6).getName());
        assertEquals("cx-contest-5", pageData.getReturnedContests().get(6).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(6).getContestResultsCount());

        assertEquals("Bx Contest 3", pageData.getReturnedContests().get(7).getName());
        assertEquals("bx-contest-3", pageData.getReturnedContests().get(7).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(7).getContestResultsCount());

        assertEquals("Bx Contest 4", pageData.getReturnedContests().get(8).getName());
        assertEquals("bx-contest-4", pageData.getReturnedContests().get(8).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(8).getContestResultsCount());

        assertEquals("Cx Alias 6", pageData.getReturnedContests().get(9).getName());
        assertEquals("aa-contest-6", pageData.getReturnedContests().get(9).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(9).getContestResultsCount());

        assertEquals("Cx Contest 5", pageData.getReturnedContests().get(10).getName());
        assertEquals("cx-contest-5", pageData.getReturnedContests().get(10).getSlug());
        assertEquals(0, pageData.getReturnedContests().get(10).getContestResultsCount());
    }
}



