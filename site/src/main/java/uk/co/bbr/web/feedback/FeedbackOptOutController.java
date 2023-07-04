package uk.co.bbr.web.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FeedbackOptOutController {
    private final UserService userService;


    @PostMapping("https://www.brassbandresults.co.uk/acc/feedback/opt-out/{uuid:[A-Za-z0-9]{40}}")
    public String feedbackOptOut(Model model, @PathVariable("uuid") String uuid) {
        Optional<SiteUserDao> matchingUser = this.userService.fetchUserByUuid(uuid);
        if (matchingUser.isEmpty()) {
            throw NotFoundException.userNotFoundByRandomString();
        }

        this.userService.optUserOutFromFeedbackEmails(matchingUser.get());

        return "feedback/opt-out-confirm";
     }
}
