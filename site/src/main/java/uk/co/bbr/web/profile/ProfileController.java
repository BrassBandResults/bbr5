package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final SecurityService securityService;

    @GetMapping("/users/{usercode:[a-zA-Z0-9@_\\-.]+}")
    public String profileHome(Model model, @PathVariable("usercode") String usercode) {

        Optional<BbrUserDao> user = this.securityService.fetchUserByUsercode(usercode);
        if (user.isEmpty()) {
            throw new NotFoundException("No user with username " + usercode);
        }

        model.addAttribute("User", user.get());

        return "profile/home";
    }
}
