package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final PerformanceService performanceService;
    private final SecurityService securityService;
    private final UserService userService;

    @GetMapping("/users/{usercode:[a-zA-Z0-9@_\\-.]+}")
    public String publicUserPage(Model model, @PathVariable("usercode") String usercode) {

        Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(usercode);
        String currentUser = this.securityService.getCurrentUsername();
        if (user.isEmpty()) {
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        List<PerformanceDao> performances;

        switch (user.get().getContestHistoryVisibility()) {
            case PUBLIC -> performances = this.performanceService.fetchApprovedPerformancesForUser(user.get());
            case SITE_ONLY -> {
                if (currentUser != null) {
                    performances = this.performanceService.fetchApprovedPerformancesForUser(user.get());
                } else {
                    performances = new ArrayList<>();
                }
            }
            default -> performances = new ArrayList<>();
        }

        model.addAttribute("User", user.get());
        model.addAttribute("Performances", performances);

        return "users/public-user";
    }
}
