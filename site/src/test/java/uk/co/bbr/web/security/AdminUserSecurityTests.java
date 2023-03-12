package uk.co.bbr.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=security-controller-tests-h2", "spring.datasource.url=jdbc:h2:mem:security-controller-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username="admin_user", roles= { "BBR_ADMIN", "BBR_SUPERUSER", "BBR_PRO", "BBR_MEMBER" })
class AdminUserSecurityTests {

   @Autowired private TestSecurityController securityTestController;

    @Test
    void testAccessUnsecuredPageWithAdminUserSucceeds() {
        String userName = securityTestController.testPublic(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessSecuredPageWithAdminUserSucceeds() {
        String userName = securityTestController.testMember(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessProAccountPageWithAdminUserSucceeds() {
        String userName = securityTestController.testPro(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessSuperuserPageWithAdminUserSucceeds() {
        String userName = securityTestController.testSuperuser(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }

    @Test
    void testAccessAdminPageWithAdminUserSucceeds() {
        String userName = securityTestController.testAdmin(new ExtendedModelMap());
        assertEquals("test/userOnly", userName);
    }
}
