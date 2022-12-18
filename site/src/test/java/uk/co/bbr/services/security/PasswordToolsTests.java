package uk.co.bbr.services.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
public class PasswordToolsTests {

    @Test
    void testCreateSaltReturnsCorrectLength() {
        String salt1 = PasswordTools.createSalt();
        String salt2 = PasswordTools.createSalt();

        assertNotEquals(salt1, salt2);
        assertEquals(8, salt1.length());
        assertEquals(8, salt2.length());
    }

    @Test
    void testPasswordVersionReturnsCorrectVersion() {
        String latestVersion = PasswordTools.latestVersion();

        assertEquals("1", latestVersion);
    }

    @Test
    void testHashPasswordWorksSuccessfully() {
        String hashedPassword = PasswordTools.hashPassword("1", "ABCD1234", "test-user", "password1");
        assertEquals("1ae4766aa2281d7535cca49bace4f05df82095a9128774b7127e65c094db4972", hashedPassword);
    }

    @Test
    void testChangingSaltChangesHashSuccessfully() {
        String hashedPassword1 = PasswordTools.hashPassword("1", "ABCD1234", "test-user", "password1");
        String hashedPassword2 = PasswordTools.hashPassword("1", "ABCD1235", "test-user", "password1");

        assertNotEquals(hashedPassword1, hashedPassword2);
        assertEquals("1ae4766aa2281d7535cca49bace4f05df82095a9128774b7127e65c094db4972", hashedPassword1);
        assertEquals("4b86eefbee01dc2a5588b2ce119c95c1c8d4d020f7fc5ac00c775a88c9208701", hashedPassword2);
    }

    @Test
    void testChangingPasswordChangesHashSuccessfully() {
        String hashedPassword1 = PasswordTools.hashPassword("1", "ABCD1234", "test-user", "password1");
        String hashedPassword2 = PasswordTools.hashPassword("1", "ABCD1234", "test-user", "password2");

        assertNotEquals(hashedPassword1, hashedPassword2);
        assertEquals("1ae4766aa2281d7535cca49bace4f05df82095a9128774b7127e65c094db4972", hashedPassword1);
        assertEquals("6cc8dcb30f6fb750607f61109c27f5b03cfa7f359db0c70387cd2234358eb057", hashedPassword2);
    }
}
