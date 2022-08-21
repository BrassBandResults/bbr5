package uk.co.bbr.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-controller-tests-h2", "spring.datasource.url=jdbc:h2:mem:security-controller-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username="member_user", roles= { "BBR_MEMBER" })
class AuthenticatedUserSecurityTests {

    @Autowired private TestSecurityController securityTestController;

    @Test
    void testAccessUnsecuredPageWithAuthenticatedUserSucceeds() {
        String userName = securityTestController.testPublic(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessSecuredPageWithAuthenticatedUserSucceeds() {
        String userName = securityTestController.testMember(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessProAccountPageWithAuthenticatedUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> securityTestController.testPro(new ExtendedModelMap()));
        assertEquals("Access is denied", thrown.getMessage());
    }

    @Test
    void testAccessSuperuserPageWithAuthenticatedUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> securityTestController.testSuperuser(new ExtendedModelMap()));
        assertEquals("Access is denied", thrown.getMessage());
    }

    @Test
    void testAccessAdminPageWithAuthenticatedUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> securityTestController.testAdmin(new ExtendedModelMap()));
        assertEquals("Access is denied", thrown.getMessage());
    }

}
