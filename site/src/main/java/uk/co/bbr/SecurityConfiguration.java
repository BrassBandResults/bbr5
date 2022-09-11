package uk.co.bbr;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.web.security.filter.SecurityFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtService jwtService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SecurityFilter jwtFilter = new SecurityFilter(this.jwtService);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().httpBasic()
                .and().csrf().csrfTokenRepository(csrfTokenRepository())
                .disable().authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/bbr-admin/**").authenticated()
                .and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(SecurityFilter.URL_SIGN_IN))
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(SecurityFilter.URL_SIGN_OUT))
                .logoutSuccessUrl(SecurityFilter.URL_SIGN_IN)
                .invalidateHttpSession(true)
                .deleteCookies(SecurityFilter.COOKIE_NAME)
                .and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico");
    }
}
