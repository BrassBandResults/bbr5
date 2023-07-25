package uk.co.bbr.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.payments.StripeService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.PendingUserDao;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;
    private final PaymentsService paymentsService;

    @IsBbrAdmin
    @GetMapping("/user-list")
    public String userList(Model model) {
        List<SiteUserDao> users = this.userService.findAll();

        model.addAttribute("Users", users);
        model.addAttribute("Type", "all");
        return "users/list";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/pro")
    public String proUserList(Model model) {
        List<SiteUserDao> users = this.userService.findAllPro();
        List<SiteUserProDao> proUsers = this.paymentsService.markupUsers(users);

        model.addAttribute("ProUsers", proUsers);
        model.addAttribute("Type", "pro");
        return "users/list-pro";
    }

    @IsBbrAdmin
    @GetMapping("user-list/pro/{usercode:[\\-@A-Za-z.\\d]+}/remove")
    public String proUserDisable(@PathVariable("usercode") String usercode) {
        Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(usercode);
        if (user.isEmpty()) {
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        this.securityService.removeProFlag(user.get());
        return "redirect:/user-list/pro";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/superuser")
    public String superUserList(Model model) {
        List<SiteUserDao> users = this.userService.findAllSuperuser();

        model.addAttribute("Users", users);
        model.addAttribute("Type", "superuser");
        return "users/list";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/admin")
    public String adminUserList(Model model) {
        List<SiteUserDao> users = this.userService.findAllAdmin();

        model.addAttribute("Users", users);
        model.addAttribute("Type", "admin");
        return "users/list";
    }

    @IsBbrAdmin
    @GetMapping("/user-list/unactivated")
    public String unactivatedUserList(Model model) {
        List<PendingUserDao> users = this.userService.listUnactivatedUsers();

        model.addAttribute("Users", users);
        return "users/list-unactivated";
    }
}
