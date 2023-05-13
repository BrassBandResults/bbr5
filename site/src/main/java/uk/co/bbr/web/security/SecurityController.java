package uk.co.bbr.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.SessionKeys;
import uk.co.bbr.web.security.filter.SecurityFilter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;
    private final JwtService jwtService;

    @GetMapping(SecurityFilter.URL_SIGN_IN)
    public String signInGet(Model model, HttpServletRequest request) {

        String nextUrl = request.getParameter("next");
        if (nextUrl == null) {
            nextUrl = "";
        }

        model.addAttribute("Next", nextUrl);

        return "security/sign-in";
    }

    @PostMapping(SecurityFilter.URL_SIGN_IN)
    public String signInPost(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String plaintextPassword) {
        try {
            BbrUserDao user = this.securityService.authenticate(username, plaintextPassword);

            Cookie securityCookie = buildLoginCookie(user);
            response.addCookie(securityCookie);

            String nextUrl = (String)request.getSession().getAttribute(SessionKeys.LOGIN_NEXT_PAGE);

            if (nextUrl != null && nextUrl.trim().length() > 0 && nextUrl.startsWith("/")) {
                return "redirect:" + nextUrl;
            }

            return "redirect:/";
        } catch (AuthenticationFailedException e) {
            model.addAttribute("LoginError", e.getMessage());
            return this.signInGet(model, request);
        }
    }

    private Cookie buildLoginCookie(BbrUserDao user) {
        String jwt = "";
        if (user != null) {
            jwt = this.jwtService.createJwt(user);
        }

        Cookie securityCookie = new Cookie(SecurityFilter.COOKIE_NAME, jwt);
        securityCookie.setMaxAge(-1);
        securityCookie.setPath("/");
        securityCookie.setHttpOnly(true);

        return securityCookie;
    }
}
