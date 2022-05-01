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
@WithMockUser(username="pro_user", roles= { "BBR_PRO" })
class ProUserSecurityTests {

    @Autowired
    private SecurityTestController securityTestController;

    @Test
    void testAccessUnsecuredPageWithProUserSucceeds() {
        String userName = securityTestController.testPublic();
        assertEquals("pro_user", userName);

    }

    @Test
    void testAccessSecuredPageWithProUserSucceeds() {
        String userName = securityTestController.testMember();
        assertEquals("pro_user", userName);

    }

    @Test
    void testAccessProAccountPageWithProUserSucceeds() {
        String userName = securityTestController.testPro();
        assertEquals("pro_user", userName);

    }

    @Test
    void testAccessAdminPageWithProUserFails() {
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
            securityTestController.testAdmin();
        });
    }

}
