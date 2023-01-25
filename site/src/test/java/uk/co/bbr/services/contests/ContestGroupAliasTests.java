package uk.co.bbr.services.contests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.dao.ContestGroupAliasDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=group-alt-name-tests-h2", "spring.datasource.url=jdbc:h2:mem:group-alt-name-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class ContestGroupAliasTests implements LoginMixin {

    @Autowired private ContestGroupService contestGroupService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreateAlternativeNameWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        ContestGroupDao contestGroup = this.contestGroupService.create(" New   Contest  Group 1 ");
        ContestGroupAliasDao contestGroupAlias = new ContestGroupAliasDao();
        contestGroupAlias.setName("  Test   Alias  1");

        // act
        ContestGroupAliasDao createdAlias = this.contestGroupService.createAlias(contestGroup, contestGroupAlias);

        // assert
        assertEquals("Test Alias 1", createdAlias.getName());
        assertEquals("New Contest Group 1", createdAlias.getContestGroup().getName());
        assertNotNull(createdAlias.getCreatedBy());
        assertNotNull(createdAlias.getUpdatedBy());
        assertNotNull(createdAlias.getCreated());
        assertNotNull(createdAlias.getUpdated());

        logoutTestUser();
    }

    @Test
    void testAliasExistsWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        ContestGroupDao contestGroup = this.contestGroupService.create("   Test Contest Group 2  ");
        ContestGroupAliasDao contestGroupAlias = new ContestGroupAliasDao();
        contestGroupAlias.setName("  Test   Alias  2 ");
        ContestGroupAliasDao createdAlias = this.contestGroupService.createAlias(contestGroup, contestGroupAlias);

        // act
        Optional<ContestGroupAliasDao> checkedGroup = this.contestGroupService.aliasExists(contestGroup, "  Test   Alias   2   ");

        // assert
        assertTrue(checkedGroup.isPresent());
        assertFalse(checkedGroup.isEmpty());
        assertEquals("Test Contest Group 2", checkedGroup.get().getContestGroup().getName());
        assertEquals("Test Alias 2", checkedGroup.get().getName());
        assertNotNull(checkedGroup.get().getCreatedBy());
        assertNotNull(checkedGroup.get().getUpdatedBy());
        assertNotNull(checkedGroup.get().getCreated());
        assertNotNull(checkedGroup.get().getUpdated());

        logoutTestUser();
    }
}


