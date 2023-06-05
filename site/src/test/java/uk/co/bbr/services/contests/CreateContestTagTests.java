package uk.co.bbr.services.contests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=contest-tag-tests-h2", "spring.datasource.url=jdbc:h2:mem:contest-tag-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
class CreateContestTagTests implements LoginMixin {

    @Autowired private ContestTagService contestTagService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreateContestTagWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        // act
        ContestTagDao contestTag = this.contestTagService.create(" New   Contest  Tag  ");

        // assert
        assertEquals("New Contest Tag", contestTag.getName());
        assertEquals("new-contest-tag", contestTag.getSlug());

        logoutTestUser();
    }

    @Test
    void testCreateTagWithSameSlugAsExistingFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestTagDao contestTag = this.contestTagService.create("Tag 1");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {ContestTagDao tagDuplicate = this.contestTagService.create("Tag 1");});

        // assert
        assertEquals("Contest Tag with slug tag-1 already exists.", ex.getMessage());

        logoutTestUser();
    }

    @Test
    void testCreateTagWithSameNameAsExistingFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestTagService.create("Tag 2");

        ContestTagDao secondTag = new ContestTagDao();
        secondTag.setName("Tag 2");
        secondTag.setSlug("different-slug");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {this.contestTagService.create(secondTag);});

        // assert
        assertEquals("Contest Tag with name Tag 2 already exists.", ex.getMessage());

        logoutTestUser();
    }
}


