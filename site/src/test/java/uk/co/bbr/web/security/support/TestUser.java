package uk.co.bbr.web.security.support;

import lombok.Getter;

public enum TestUser {
    TEST_MEMBER("test_user_member", "password_member", "test.member@brassbandresults.co.uk", ""),
    TEST_PRO("test_user_pro", "password_pro", "test.pro@brassbandresults.co.uk", ""),
    TEST_SUPERUSER("test_user_superuser", "password_superuser", "test.superuser@brassbandresults.co.uk", ""),
    TEST_ADMIN("user_admin", "password_admin", "test.admin@brassbandresults.co.uk", ""),
    TEST_INVALID("user_invalid", "NotAPassword", "test.invalid@brassbandresults.co.uk", ""),
    DJANGO_MEMBER("django", "mystery", "test.django@brassbandresults.co.uk", "pbkdf2_sha256$10000$qx1ec0f4lu4l$3G81rAm/4ng0tCCPTrx2aWohq7ztDBfFYczGNoUtiKQ=")
    ;

    @Getter private final String username;
    @Getter private final String password;
    @Getter private final String email;
    @Getter private final String hash;

    TestUser(String username, String password, String email, String hash) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.hash = hash;
    }
}
