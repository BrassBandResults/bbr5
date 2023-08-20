package uk.co.bbr;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.web.security.filter.SecurityFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true)
public class SecurityConfiguration {
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SecurityFilter jwtFilter = new SecurityFilter(this.jwtService);

        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        // set the name of the attribute the CsrfToken will be populated on
        delegate.setCsrfRequestAttributeName("_csrf");
        // Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
        CsrfTokenRequestHandler requestHandler = delegate::handle;

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        .and().authorizeHttpRequests(authz -> authz
              .requestMatchers("/**").permitAll())
              .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(SecurityFilter.URL_SIGN_IN))
        .and()
            .httpBasic()
        .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher(SecurityFilter.URL_SIGN_OUT))
            .logoutSuccessUrl(SecurityFilter.URL_SIGN_IN)
            .invalidateHttpSession(true)
            .deleteCookies(SecurityFilter.COOKIE_NAME)
        .and()
            .csrf(csrf -> csrf.csrfTokenRepository(this.csrfTokenRepository()).csrfTokenRequestHandler(requestHandler).disable())
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static final class CsrfCookieFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            // Render the token value to a cookie by causing the deferred token to be loaded
            if (csrfToken != null) {
                System.out.println("!!!" + csrfToken.getToken());
            }

            filterChain.doFilter(request, response);
        }

    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }
}
