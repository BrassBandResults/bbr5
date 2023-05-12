package uk.co.bbr.web.security.filter;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.web.SessionKeys;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class SecurityFilter extends GenericFilterBean {
    public static final String COOKIE_NAME = "BrassBandResultsUser";

    public static final String URL_SIGN_IN = "/acc/signin";
    public static final String URL_SIGN_OUT = "/acc/signout";

    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    private final JwtService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest)request;

        String nextPage = servletRequest.getServletPath();
        if (!SecurityFilter.URL_SIGN_IN.equals(nextPage)) {
            servletRequest.getSession().setAttribute(SessionKeys.LOGIN_NEXT_PAGE, nextPage);
        }

        if (servletRequest.getServletPath().startsWith(SecurityFilter.URL_SIGN_IN)) {
            chain.doFilter(request, response);
            return;
        }

        if (servletRequest.getServletPath().startsWith("/error")) {
            chain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            Optional<Cookie> securityCookie = Arrays.stream(cookies).filter(n -> n.getName().equals(COOKIE_NAME)).findFirst();

            if (securityCookie.isPresent()) {
                String jwtEncoded = securityCookie.get().getValue();

                try {
                    DecodedJWT jwt = this.jwtService.verifyJwt(jwtEncoded);

                    // Token is valid, set auth context
                    final Authentication auth = this.jwtService.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // proceed to next filter in chain
                    chain.doFilter(request, response);
                } catch (final SignatureVerificationException | InvalidClaimException ex) {
                    // Token failed validation
                    ((HttpServletResponse) response).setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Invalid user session");
                }
            } else {
                // no token, pass request on
                chain.doFilter(request, response);
            }
        }
        else {
            // no cookies, pass request on
            chain.doFilter(request, response);
        }
    }
}
