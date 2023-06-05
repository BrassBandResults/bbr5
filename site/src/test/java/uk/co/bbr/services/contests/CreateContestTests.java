package uk.co.bbr.services.contests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=create-contest-tests-h2", "spring.datasource.url=jdbc:h2:mem:create-contest-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
class CreateContestTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private ContestTypeService contestTypeService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreatingSingleContestEventWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        // act
        ContestDao contest = this.contestService.create("  North  West   Area   ");

        // assert
        assertEquals("North West Area", contest.getName());
        assertEquals("north-west-area", contest.getSlug());
        assertNotNull(contest.getId());
        assertNotNull(contest.getDefaultContestType());
        assertEquals(this.contestTypeService.fetchDefaultContestType().getSlug(), contest.getDefaultContestType().getSlug());

        logoutTestUser();
    }
}


