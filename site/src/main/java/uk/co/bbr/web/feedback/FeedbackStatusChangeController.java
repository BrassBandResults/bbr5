package uk.co.bbr.web.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FeedbackStatusChangeController {
    private final FeedbackService feedbackService;
    private final SecurityService securityService;
    private final UserService userService;

    @IsBbrSuperuser
    @GetMapping("/feedback/status-change/{type:[a-z]+}/{usercode:[\\-@A-Za-z.\\d]+}/{feedbackId:\\d+}")
    public String changeStatus(@PathVariable("type") String type, @PathVariable("usercode") String usercode, @PathVariable("feedbackId") Long feedbackId) {
        Optional<FeedbackDao> feedback = this.feedbackService.fetchById(feedbackId);
        if (feedback.isEmpty()) {
            throw NotFoundException.feedbackNotFoundById(feedbackId);
        }
        Optional<SiteUserDao> destinationUser = this.userService.fetchUserByUsercode(usercode);
        if (destinationUser.isEmpty()) {
            throw NotFoundException.userNotFoundByUsercode(usercode);
        }

        String currentUsername = this.securityService.getCurrentUsername();

        switch(type) {
            case "claim":
                feedback.get().assignToUser(currentUsername, destinationUser.get());
                break;
            case "done":
                feedback.get().markDone(currentUsername);
                break;
            case "owner":
                feedback.get().sendToOwner(currentUsername);
                break;
            case "closed":
                feedback.get().markClosed(currentUsername);
                break;
            case "inconclusive":
                feedback.get().markInconclusive(currentUsername);
                break;
            case "spam":
                feedback.get().markAsSpam(currentUsername);
                break;
            default:
                throw NotFoundException.feedbackUpdateNotFound(type);
        }

        this.feedbackService.update(feedback.get());

        return "redirect:/feedback/queue";
    }
}
