package uk.co.bbr.services.contests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contets-create-group-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class CreateContestGroupTests implements LoginMixin {

    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreateContestGroupWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        // act
        ContestGroupDao contestGroup = this.contestGroupService.create(" New   Contest  Group  ");

        // assert
        assertEquals("New Contest Group", contestGroup.getName());
        assertEquals("NEW-CONTEST-GROUP", contestGroup.getSlug());

        logoutTestUser();
    }

    @Test
    void testCreateGroupWithSameSlugAsExistingFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestGroupDao contestGroup = this.contestGroupService.create("Group 1");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {ContestGroupDao GroupDuplicate = this.contestGroupService.create("Group 1");});

        // assert
        assertEquals("Contest Group with slug GROUP-1 already exists.", ex.getMessage());

        logoutTestUser();
    }

    @Test
    void testCreateGroupWithSameNameAsExistingFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestGroupService.create("Group 2");

        ContestGroupDao secondGroup = new ContestGroupDao();
        secondGroup.setName("Group 2");
        secondGroup.setSlug("different-slug");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {this.contestGroupService.create(secondGroup);});

        // assert
        assertEquals("Contest Group with name Group 2 already exists.", ex.getMessage());

        logoutTestUser();
    }

    @Test
    void testAddTagsToContestGroupWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestGroupDao group = this.contestGroupService.create("Group 3");
        ContestTagDao tag = this.contestTagService.create("Tag 1");

        // act
        ContestGroupDao savedGroup = this.contestGroupService.addGroupTag(group, tag);

        // assert
        assertEquals("Group 3", savedGroup.getName());
        assertEquals("GROUP-3", savedGroup.getSlug());
        assertNotNull(savedGroup.getId());
        assertEquals(1, savedGroup.getTags().size());
        assertEquals("Tag 1", savedGroup.getTags().stream().findFirst().get().getName());

        logoutTestUser();
    }
}


