package uk.co.bbr.services.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import uk.co.bbr.services.security.dao.BbrUserDao;

public interface JwtService {
    String createJwt(BbrUserDao user);

    DecodedJWT verifyJwt(String jwt);

    Authentication getAuthentication(DecodedJWT jwt);
}
