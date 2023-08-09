package uk.co.bbr;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.web.security.filter.SecurityFilter;

@Configuration
@RequiredArgsConstructor
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

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().authorizeHttpRequests((authz) -> authz
              .requestMatchers("/**").permitAll()).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
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
            .csrf((csrf) -> csrf.csrfTokenRepository(this.csrfTokenRepository()).csrfTokenRequestHandler(requestHandler));

        return http.build();

//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and().httpBasic()
//                .and().csrf().csrfTokenRepository(this.csrfTokenRepository())
//                .disable().authorizeRequests()
//                .requestMatchers("/bbr-admin/**").authenticated()
//                .requestMatchers("/migrate/**").authenticated()
//                .requestMatchers("/**").permitAll()
//                .and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(SecurityFilter.URL_SIGN_IN))
//                .and().logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher(SecurityFilter.URL_SIGN_OUT))
//                .logoutSuccessUrl(SecurityFilter.URL_SIGN_IN)
//                .invalidateHttpSession(true)
//                .deleteCookies(SecurityFilter.COOKIE_NAME)
//                .and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }
}
