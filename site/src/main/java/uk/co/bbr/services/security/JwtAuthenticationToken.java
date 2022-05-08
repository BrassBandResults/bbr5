package uk.co.bbr.services.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationToken implements Authentication {

    public static final String CLAIM_USER_ID = "userid";
    public static final String CLAIM_USER_NAME = "name";
    public static final String CLAIM_ROLE = "role";

    public static final String ROLE_TEXT_ADMIN = "admin";
    public static final String ROLE_TEXT_SUPERUSER = "superuser";
    public static final String ROLE_TEXT_PRO = "pro";
    public static final String ROLE_TEXT_MEMBER = "member";

    public static final String GRANTED_AUTH_ADMIN = "ROLE_BBR_ADMIN";
    public static final String GRANTED_AUTH_SUPERUSER = "ROLE_BBR_SUPERUSER";
    public static final String GRANTED_AUTH_PRO = "ROLE_BBR_PRO";
    public static final String GRANTED_AUTH_MEMBER = "ROLE_BBR_MEMBER";

    private final DecodedJWT jwt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        String role = this.jwt.getClaim(CLAIM_ROLE).asString();
        if (role != null && !role.isEmpty()) {
            switch (role) {
                case ROLE_TEXT_ADMIN:
                    authorities.add(new SimpleGrantedAuthority(GRANTED_AUTH_ADMIN));
                    break;
                case ROLE_TEXT_SUPERUSER:
                    authorities.add(new SimpleGrantedAuthority(GRANTED_AUTH_SUPERUSER));
                    break;
                case ROLE_TEXT_PRO:
                    authorities.add(new SimpleGrantedAuthority(GRANTED_AUTH_PRO));
                    break;
                case ROLE_TEXT_MEMBER:
                    authorities.add(new SimpleGrantedAuthority(GRANTED_AUTH_MEMBER));
                    break;
            }
        }

        return authorities;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getDetails() {
        Map<String, String> details = new HashMap<>();
        details.put(CLAIM_ROLE, this.jwt.getClaim(CLAIM_ROLE).asString());
        return details;
    }


    @Override
    public Object getPrincipal() {
        return this.jwt.getClaim(CLAIM_USER_ID).asLong();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getName() {
        return this.jwt.getClaim(CLAIM_USER_NAME).asString();
    }
}
