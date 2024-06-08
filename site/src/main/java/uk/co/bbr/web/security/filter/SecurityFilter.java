package uk.co.bbr.web.security.filter;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.web.SessionKeys;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class SecurityFilter extends GenericFilterBean {
    public static final String COOKIE_NAME = "BrassBandResultsUser";

    public static final String URL_SIGN_IN = "/acc/sign-in";
    public static final String URL_SIGN_OUT = "/acc/sign-out";

    public static final String CSRF_HEADER_NAME = "XSRF-TOKEN";

    private final JwtService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest)request;

        if (servletRequest.getServletPath().startsWith(SecurityFilter.URL_SIGN_IN)) {
            chain.doFilter(request, response);
            return;
        }

        if (servletRequest.getServletPath().startsWith("/error")) {
            chain.doFilter(request, response);
            return;
        }

        this.authWebRequest(servletRequest, response, chain);
    }

    private void authWebRequest(HttpServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String nextPage = servletRequest.getServletPath();
        if (!nextPage.startsWith("/acc/")) {
            servletRequest.getSession().setAttribute(SessionKeys.LOGIN_NEXT_PAGE, nextPage);
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
                    chain.doFilter(servletRequest, response);
                } catch (final SignatureVerificationException | InvalidClaimException | JWTDecodeException ex) {
                    // Token failed validation
                    securityCookie.get().setMaxAge(0);
                    ((HttpServletResponse) response).setStatus(HttpStatus.FORBIDDEN.value());
                    ((HttpServletResponse) response).addCookie(securityCookie.get());
                    response.getWriter().write("Invalid user session - reload the page to logout");
                }
            } else {
                // no token, pass request on
                chain.doFilter(servletRequest, response);
            }
        } else {
            // no cookies, pass request on
            chain.doFilter(servletRequest, response);
        }
    }
}
