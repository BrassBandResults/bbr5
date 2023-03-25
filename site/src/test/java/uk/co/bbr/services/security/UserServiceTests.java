package uk.co.bbr.services.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=user-tests-h2", "spring.datasource.url=jdbc:h2:mem:user-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
class UserServiceTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreateUser() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        // act
        this.securityService.createUser("test_User", "testPassword", "test@brassbandresults.co.uk");

        // assert
        Optional<BbrUserDao> fetchedUser = this.securityService.fetchUserByUsercode("test_User");
        assertTrue(fetchedUser.isPresent());
        assertEquals("test_User", fetchedUser.get().getUsercode());
        assertEquals("1", fetchedUser.get().getPasswordVersion());
        assertNotNull(fetchedUser.get().getSalt());
        assertNotNull(fetchedUser.get().getCreated());
        assertNotNull(fetchedUser.get().getUpdated());
        assertNotNull(fetchedUser.get().getId());
        assertNotNull(fetchedUser.get().getPassword());
        assertNotEquals("testPassword", fetchedUser.get().getPassword());
        assertEquals("M", fetchedUser.get().getAccessLevel());
        assertEquals("test@brassbandresults.co.uk", fetchedUser.get().getEmail());
        assertEquals("owner", fetchedUser.get().getCreatedBy());
        assertEquals("owner", fetchedUser.get().getUpdatedBy());

        logoutTestUser();
    }
}
