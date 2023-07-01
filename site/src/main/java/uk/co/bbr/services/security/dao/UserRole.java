package uk.co.bbr.services.security.dao;

import lombok.Getter;
import uk.co.bbr.services.security.JwtAuthenticationToken;

public enum UserRole {
    ADMIN(JwtAuthenticationToken.ROLE_TEXT_ADMIN, "A"),
    SUPERUSER(JwtAuthenticationToken.ROLE_TEXT_SUPERUSER, "S"),
    PRO(JwtAuthenticationToken.ROLE_TEXT_PRO, "P"),
    MEMBER(JwtAuthenticationToken.ROLE_TEXT_MEMBER, "M"),
    NO_ACCESS(null, "0"),
    ;

    UserRole(String text, String code) {
        this.roleText = text;
        this.code = code;
    }

    private final String roleText;
    @Getter
    private final String code;

    public static UserRole fromCode(String accessLevel) {
        for (UserRole ts : UserRole.values()) {
            if (ts.code.equals(accessLevel)) {
                return ts;
            }
        }
        throw new IllegalArgumentException("No UserRole with code " + accessLevel + " found");
    }

    public String asString() {
        return this.roleText;
    }
}

