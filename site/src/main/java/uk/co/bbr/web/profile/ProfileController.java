package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    @GetMapping("/user/{usercode:.+")
    public String profileHome(Model model, @PathVariable("usercode") String usercode) {
        return "profile/home";
    }
}
