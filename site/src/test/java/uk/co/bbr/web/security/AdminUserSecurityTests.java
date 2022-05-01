package uk.co.bbr.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-controller-tests-h2", "spring.datasource.url=jdbc:h2:mem:security-controller-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@WithMockUser(username="admin_user", roles= { "BBR_ADMIN" })
class AdminUserSecurityTests {

    @Autowired
    private SecurityTestController securityTestController;

    @Test
    void testAccessUnsecuredPageWithAdminUserSucceeds() {
        String userName = securityTestController.testPublic();
        assertEquals("admin_user", userName);
    }

    @Test
    void testAccessSecuredPageWithAdminUserSucceeds() {
        String userName = securityTestController.testMember();
        assertEquals("admin_user", userName);

    }

    @Test
    void testAccessProAccountPageWithAdminUserSucceeds() {
        String userName = securityTestController.testPro();
        assertEquals("admin_user", userName);

    }

    @Test
    void testAccessAdminPageWithAdminUserSucceeds() {
        String userName = securityTestController.testAdmin();
        assertEquals("admin_user", userName);
    }

}
