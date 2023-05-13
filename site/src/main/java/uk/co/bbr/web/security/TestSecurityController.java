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
        return this.returnTest(model);
    }

    @GetMapping("/test/member")
    @IsBbrMember
    public String testMember(Model model) {
        return this.returnTest(model);
    }

    @GetMapping("/test/pro")
    @IsBbrPro
    public String testPro(Model model) {
        return this.returnTest(model);
    }

    @GetMapping("/test/superuser")
    @IsBbrSuperuser
    public String testSuperuser(Model model) {
        return this.returnTest(model);
    }

    @GetMapping("/test/admin")
    @IsBbrAdmin
    public String testAdmin(Model model) {
        return this.returnTest(model);
    }

    private String returnTest(Model model) {
        model.addAttribute("CurrentUsername", this.securityService.getCurrentUsername());
        return "test/userOnly";
    }

}
