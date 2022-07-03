package uk.co.bbr.web.security.support;

import lombok.Getter;

public enum TestUser {
    TEST_MEMBER("test_user_member", "password_member", "tim.member@brassbandresults.co.uk"),
    TEST_PRO("test_user_pro", "password_pro", "tim.pro@brassbandresults.co.uk"),
    TEST_SUPERUSER("test_user_superuser", "password_superuser", "tim.superuser@brassbandresults.co.uk"),
    TEST_ADMIN("user_admin", "password_admin", "tim.admin@brassbandresults.co.uk"),
    TEST_INVALID("user_invalid", "NotAPassword", "tim.invalid@brassbandresults.co.uk"),
    ;

    @Getter private final String username;
    @Getter private final String password;
    @Getter private final String email;

    TestUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
