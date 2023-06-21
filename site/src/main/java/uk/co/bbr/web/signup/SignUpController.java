package uk.co.bbr.web.signup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final UserService userService;

    @GetMapping("/acc/sign-up")
    public String antiSpam() {
        return "signup/spam-check";
    }

    @PostMapping("/acc/sign-up")
    public String antiSpamPost(@RequestParam("section") String section) {

        if (section.equalsIgnoreCase("bs")) {
            return "redirect:/acc/register";
        }

        return "redirect:/acc/login-pro";
    }

    @GetMapping("/acc/register")
    public String register(Model model) {

        model.addAttribute("Errors", "");
        model.addAttribute("Email", "");
        model.addAttribute("Username", "");

        return "signup/register";
    }

    @PostMapping("/acc/register")
    public String registerPost(Model model, @RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password1") String password1, @RequestParam("password2") String password2) {

        Optional<BbrUserDao> existingUser = this.userService.fetchUserByUsercode(username);
        if (existingUser.isPresent()) {
            model.addAttribute("Errors", "page.signup.errors.username-already-used");
            model.addAttribute("Email", email);
            model.addAttribute("Username", username);
            return "signup/register";
        }

        if (!password1.equals(password2)) {
            model.addAttribute("Errors", "page.signup.errors.passwords-dont-match");
            model.addAttribute("Email", email);
            model.addAttribute("Username", username);
            return "signup/register";
        }

        if (password1.trim().length() < 8) {
            model.addAttribute("Errors", "page.signup.errors.password-too-short");
            model.addAttribute("Email", email);
            model.addAttribute("Username", username);
            return "signup/register";
        }

        return "redirect:/acc/sign-up-confirm";
    }
}
