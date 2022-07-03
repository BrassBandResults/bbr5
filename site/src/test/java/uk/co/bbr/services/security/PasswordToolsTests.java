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
        assertEquals("c8f5e70f9d9bc00c2a9b27fc2e720965231a5508613f68778d30d39a529e8121", hashedPassword);
    }

    @Test
    void testChangingSaltChangesHashSuccessfully() {
        String hashedPassword1 = PasswordTools.hashPassword("1", "ABCD1234", "test-user", "password1");
        String hashedPassword2 = PasswordTools.hashPassword("1", "ABCD1235", "test-user", "password1");

        assertNotEquals(hashedPassword1, hashedPassword2);
        assertEquals("c8f5e70f9d9bc00c2a9b27fc2e720965231a5508613f68778d30d39a529e8121", hashedPassword1);
        assertEquals("d586e5dc6d7a5f8946e3061124b39e9eb1ce92618675788b301f3b462bb7da4f", hashedPassword2);
    }

    @Test
    void testChangingPasswordChangesHashSuccessfully() {
        String hashedPassword1 = PasswordTools.hashPassword("1", "ABCD1234", "test-user", "password1");
        String hashedPassword2 = PasswordTools.hashPassword("1", "ABCD1234", "test-user", "password2");

        assertNotEquals(hashedPassword1, hashedPassword2);
        assertEquals("c8f5e70f9d9bc00c2a9b27fc2e720965231a5508613f68778d30d39a529e8121", hashedPassword1);
        assertEquals("1ae04ffddf119487deead3315ea0290a3767b2361dd5a06365f384d8dcaebd86", hashedPassword2);
    }
}
