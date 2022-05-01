package uk.co.bbr.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-controller-tests-h2", "spring.datasource.url=jdbc:h2:mem:security-controller-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@WithAnonymousUser
class AnonymousUserSecurityTests {

    @Autowired
    private SecurityTestController securityTestController;

    @Test
    void testAccessUnsecuredPageWithAnonymousUserSucceeds() {
        String userName = securityTestController.testPublic();
        assertEquals("anonymous", userName);

    }

    @Test
    void testAccessSecuredPageWithAnonymousUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
            securityTestController.testMember();
        });

    }

    @Test
    void testAccessProAccountPageWithAnonymousUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
            securityTestController.testPro();
        });
    }

    @Test
    void testAccessAdminPageWithAnonymousUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
            securityTestController.testAdmin();
        });

    }

}
