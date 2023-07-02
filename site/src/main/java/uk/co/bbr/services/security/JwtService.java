package uk.co.bbr.services.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import uk.co.bbr.services.security.dao.SiteUserDao;

public interface JwtService {
    String createJwt(SiteUserDao user);

    DecodedJWT verifyJwt(String jwt);

    Authentication getAuthentication(DecodedJWT jwt);
}
