package uk.co.bbr.services.security.dao;

import uk.co.bbr.services.security.JwtAuthenticationToken;

public enum UserRole {
    ADMIN(JwtAuthenticationToken.ROLE_TEXT_ADMIN),
    SUPERUSER(JwtAuthenticationToken.ROLE_TEXT_SUPERUSER),
    PRO(JwtAuthenticationToken.ROLE_TEXT_PRO),
    MEMBER(JwtAuthenticationToken.ROLE_TEXT_MEMBER);

    UserRole(String text) {
        this.roleText = text;
    }

    private final String roleText;

    public String asString() {
        return this.roleText;
    }
}

