package uk.co.bbr.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;
import uk.co.bbr.web.security.annotations.IsBbrPro;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

@Controller
@RequiredArgsConstructor
public class TestSecurityController {

    private final SecurityService securityService;

    @GetMapping("/test/public")
    public String testPublic(Model model) {
        model.addAttribute("CurrentUsername", this.securityService.getCurrentUsername());
        return "test/userOnly";
    }

    @GetMapping("/test/member")
    @IsBbrMember
    public String testMember(Model model) {
        model.addAttribute("CurrentUsername", this.securityService.getCurrentUsername());
        return "test/userOnly";

    }

    @GetMapping("/test/pro")
    @IsBbrPro
    public String testPro(Model model) {
        model.addAttribute("CurrentUsername", this.securityService.getCurrentUsername());
        return "test/userOnly";

    }

    @GetMapping("/test/superuser")
    @IsBbrSuperuser
    public String testSuperuser(Model model) {
        model.addAttribute("CurrentUsername", this.securityService.getCurrentUsername());
        return "test/userOnly";

    }

    @GetMapping("/test/admin")
    @IsBbrAdmin
    public String testAdmin(Model model) {
        model.addAttribute("CurrentUsername", this.securityService.getCurrentUsername());
        return "test/userOnly";

    }

}
