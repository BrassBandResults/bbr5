package uk.co.bbr.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.bbr.services.security.SecurityService;

@Controller
@RequiredArgsConstructor
public class SecurityTestController {

    private final SecurityService securityService;

    @GetMapping("/test/public")
    public String testPublic() {
        return this.securityService.getCurrentUsername();
    }

    @GetMapping("/test/member")
    @IsBbrMember
    public String testMember() {
        return this.securityService.getCurrentUsername();
    }

    @GetMapping("/test/pro")
    @IsBbrPro
    public String testPro() {
        return this.securityService.getCurrentUsername();
    }

    @GetMapping("/test/admin")
    @IsBbrAdmin
    public String testAdmin() {
        return this.securityService.getCurrentUsername();
    }

}
