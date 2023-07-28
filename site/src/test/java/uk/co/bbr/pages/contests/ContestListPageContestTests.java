package uk.co.bbr.pages.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.contests.sql.dto.ContestListSqlDto;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-list-contest-pages-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestListPageContestTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestService.create("Ab Contest 1");
        this.contestService.create("Ac Contest 2");
        this.contestService.create("Bx Contest 3");
        this.contestService.create("Bx Contest 4");
        this.contestService.create("Cx Contest 5");
        this.contestService.create("Aa Contest 6");

        logoutTestUser();
    }


    @Test
    void testFetchContestListWithPrefixWorksSuccessfullyForContests() {
        // act
        List<ContestListSqlDto> pageData = this.contestService.listContestsStartingWith("A");

        // assert
        assertEquals(3, pageData.size());
        assertEquals("Aa Contest 6", pageData.get(0).getName());
        assertEquals(0, pageData.get(0).getEventCount());

        assertEquals("Ab Contest 1", pageData.get(1).getName());
        assertEquals(0, pageData.get(1).getEventCount());

        assertEquals("Ac Contest 2", pageData.get(2).getName());
        assertEquals(0, pageData.get(2).getEventCount());
    }

    @Test
    void testFetchContestListLAllWorksSuccessfullyForContests() {
        // act
        List<ContestListSqlDto> pageData = this.contestService.listContestsStartingWith("ALL");

        // assert
        assertEquals(6, pageData.size());
        assertEquals("Aa Contest 6", pageData.get(0).getName());
        assertEquals(0, pageData.get(0).getEventCount());

        assertEquals("Ab Contest 1", pageData.get(1).getName());
        assertEquals(0, pageData.get(1).getEventCount());

        assertEquals("Ac Contest 2", pageData.get(2).getName());
        assertEquals(0, pageData.get(2).getEventCount());

        assertEquals("Bx Contest 3", pageData.get(3).getName());
        assertEquals(0, pageData.get(3).getEventCount());

        assertEquals("Bx Contest 4", pageData.get(4).getName());
        assertEquals(0, pageData.get(4).getEventCount());

        assertEquals("Cx Contest 5", pageData.get(5).getName());
        assertEquals(0, pageData.get(5).getEventCount());
    }
}



