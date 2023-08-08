package uk.co.bbr.web.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.payments.PaymentsService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonProfileDao;
import uk.co.bbr.services.performances.PerformanceService;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MyResultsController {

    private final PerformanceService performanceService;
    private final UserService userService;

    @GetMapping("/myresults/{usercode:[a-zA-Z0-9@_\\-.]+}")
    public String publicMyResultsPage(Model model, @PathVariable("usercode") String usercode) {

        Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(usercode);
        if (user.isEmpty()) {
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        List<PerformanceDao> performances = this.performanceService.fetchApprovedPerformancesForUser(user.get());

        model.addAttribute("User", user.get());
        model.addAttribute("Performances", performances);

        return "users/myresults";
    }
}
