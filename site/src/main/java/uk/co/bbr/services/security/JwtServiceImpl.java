package uk.co.bbr.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.security.dao.SiteUserDao;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String JWT_ISSUER = "brassbandresults.co.uk";
    private static final String DEFAULT_PRIVATE_KEY = "ThisIsAPrivateKeyHorseBoltBatteryStaple";


    private String getPrivateKey() {
        String key = System.getenv("JWT_PRIVATE_KEY");
        if (key == null || key.strip().length() < 1) {
            key = DEFAULT_PRIVATE_KEY;
        }
        return key;
    }

    @Override
    public String createJwt(SiteUserDao irisUser) {
        Algorithm algorithmHS = Algorithm.HMAC256(this.getPrivateKey());
        return JWT.create().withIssuer(JWT_ISSUER)
                .withClaim(JwtAuthenticationToken.CLAIM_USER_ID, irisUser.getId())
                .withClaim(JwtAuthenticationToken.CLAIM_USER_NAME, irisUser.getUsercode())
                .withClaim(JwtAuthenticationToken.CLAIM_ROLE, irisUser.getRole().asString())
                .sign(algorithmHS);
    }

    @Override
    public DecodedJWT verifyJwt(String jwtToken) {
        Algorithm algorithmHS = Algorithm.HMAC256(this.getPrivateKey());
        JWTVerifier verifier = JWT.require(algorithmHS).withIssuer(JWT_ISSUER).build();
        return verifier.verify(jwtToken);
    }

    @Override
    public Authentication getAuthentication(DecodedJWT jwt) {
        return new JwtAuthenticationToken(jwt);
    }
}
