package uk.co.bbr.web.security;

import com.stripe.Stripe;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.payments.StripeService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.dao.PendingUserDao;
import uk.co.bbr.services.security.dao.SiteUserProDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StripeService stripeService;

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
        List<SiteUserProDao> proUsers = new ArrayList<>();

        List<SiteUserDao> users = this.userService.findAllPro();
        for (SiteUserDao user : users) {
            Subscription sub = this.stripeService.fetchSubscription(user);

            proUsers.add(new SiteUserProDao(user, sub));
        }

        model.addAttribute("ProUsers", proUsers);
        model.addAttribute("Type", "pro");
        return "users/list-pro";
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
