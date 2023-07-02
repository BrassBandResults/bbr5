package uk.co.bbr.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.email.EmailService;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ForgottenPasswordController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/acc/forgotten-password")
    public String passwordResetGet() {
        return "security/password/reset";
    }

    @PostMapping("/acc/forgotten-password")
    public String passwordResetPost(@RequestParam("usercode") String username) {
        String usercode = username.trim();
        Optional<SiteUserDao> matchingUser = this.userService.fetchUserByUsercode(usercode);
        if (matchingUser.isEmpty()) {
            matchingUser = this.userService.fetchUserByEmail(usercode);
        }

        if (matchingUser.isPresent()) {
            this.userService.generateResetPasswordKey(matchingUser.get());
            this.emailService.sendResetPasswordEmail(matchingUser.get());
        }

        return "redirect:/acc/forgotten-password/sent";
    }

    @GetMapping("/acc/forgotten-password/sent")
    public String passwordResetEmailSent() {
        return "security/password/sent";
    }

    @GetMapping("/acc/forgotten-password/reset/{resetKey:[A-Za-z0-9]{40}}")
    public String passwordChangeGet(Model model, @PathVariable("resetKey") String resetKey) {
        Optional<SiteUserDao> matchingUser = this.userService.fetchUserByResetPasswordKey(resetKey);
        if (matchingUser.isEmpty()) {
            throw NotFoundException.userNotFoundByResetPasswordKey();
        }

        model.addAttribute("ResetKey", resetKey);
        model.addAttribute("User", matchingUser.get());

        return "security/password/enter-new-password";
    }

    @PostMapping("/acc/forgotten-password/reset/{resetKey:[A-Za-z0-9]{40}}")
    public String passwordChangePost(Model model, @RequestParam("password1") String password1, @RequestParam("password2") String password2, @PathVariable("resetKey") String resetKey) {
        Optional<SiteUserDao> matchingUser = this.userService.fetchUserByResetPasswordKey(resetKey);
        if (matchingUser.isEmpty()) {
            throw NotFoundException.userNotFoundByResetPasswordKey();
        }

        if (!password1.equals(password2)) {
            model.addAttribute("Errors", "page.signup.errors.passwords-dont-match");
            model.addAttribute("User", matchingUser.get());
            return "security/password/enter-new-password";
        }

        if (password1.trim().length() < 8) {
            model.addAttribute("Errors", "page.signup.errors.password-too-short");
            model.addAttribute("User", matchingUser.get());
            return "security/password/enter-new-password";
        }

        return "redirect:/acc/forgotten-password/changed";
    }

    @GetMapping("/acc/forgotten-password/changed")
    public String passwordChanged() {
        return "security/password/changed";
    }
}
