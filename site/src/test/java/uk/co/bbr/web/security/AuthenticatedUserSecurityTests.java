package uk.co.bbr.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-controller-tests-h2", "spring.datasource.url=jdbc:h2:mem:security-controller-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@WithMockUser(username="member_user", roles= { "BBR_MEMBER" })
class AuthenticatedUserSecurityTests {

    @Autowired
    private SecurityTestController securityTestController;

    @Test
    void testAccessUnsecuredPageWithAuthenticatedUserSucceeds() {
        String userName = securityTestController.testPublic();
        assertEquals("member_user", userName);
    }

    @Test
    void testAccessSecuredPageWithAuthenticatedUserSucceeds() {
        String userName = securityTestController.testMember();
        assertEquals("member_user", userName);
    }

    @Test
    void testAccessProAccountPageWithAuthenticatedUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
            securityTestController.testPro();
        });
    }

    @Test
    void testAccessAdminPageWithAuthenticatedUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
            securityTestController.testAdmin();
        });
    }

}
