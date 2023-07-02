package uk.co.bbr.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.email.EmailService;
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
}
