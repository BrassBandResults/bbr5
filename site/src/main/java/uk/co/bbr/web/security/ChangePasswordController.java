package uk.co.bbr.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChangePasswordController {

    private final UserService userService;
    private final SecurityService securityService;

    @IsBbrMember
    @GetMapping("/acc/change-password")
    public String passwordChangeGet(Model model) {
        model.addAttribute("Errors", "");
        return "security/change-password/form";
    }

    @IsBbrMember
    @PostMapping("/acc/change-password")
    public String passwordChangePost(Model model, @RequestParam("passwordOld") String passwordOld, @RequestParam("passwordNew1") String passwordNew1, @RequestParam("passwordNew2") String passwordNew2) {
        String usercode = this.securityService.getCurrentUsername();
        Optional<SiteUserDao> matchingUser = this.userService.fetchUserByUsercode(usercode);
        if (matchingUser.isEmpty()) {
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        try {
            this.securityService.authenticate(matchingUser.get().getUsercode(), passwordOld);
        } catch (AuthenticationFailedException e) {
            model.addAttribute("Errors", "page.signup.errors.old-password-wrong");
            return "security/change-password/form";
        }

        if (!passwordNew1.equals(passwordNew2)) {
            model.addAttribute("Errors", "page.signup.errors.passwords-dont-match");
            return "security/change-password/form";
        }

        if (passwordNew1.strip().length() < 8) {
            model.addAttribute("Errors", "page.signup.errors.password-too-short");
            return "security/change-password/form";
        }

        this.userService.changePassword(matchingUser.get(), passwordNew1);

        return "redirect:/acc/change-password/changed";
    }

    @IsBbrMember
    @GetMapping("/acc/change-password/changed")
    public String passwordChanged() {
        return "security/change-password/changed";
    }
}
