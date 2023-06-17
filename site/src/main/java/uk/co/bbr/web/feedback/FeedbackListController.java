package uk.co.bbr.web.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.bbr.services.feedback.FeedbackService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrSuperuser;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FeedbackListController {
    private final FeedbackService feedbackService;
    private final SecurityService securityService;

    @IsBbrSuperuser
    @GetMapping("/feedback/queue")
    public String queue(Model model) {

        List<FeedbackDao> feedbackList = this.feedbackService.fetchSuperuserQueue();
        int ownerCount = this.feedbackService.fetchOwnerCount();
        int spamCount = this.feedbackService.fetchSpamCount();
        int inconclusiveCount = this.feedbackService.fetchInconclusiveCount();

        model.addAttribute("Feedback", feedbackList);
        model.addAttribute("FeedbackCount", feedbackList.size());
        model.addAttribute("OwnerCount", ownerCount);
        model.addAttribute("SpamCount", spamCount);
        model.addAttribute("InconclusiveCount", inconclusiveCount);
        model.addAttribute("CurrentUserCode", this.securityService.getCurrentUsername());
        model.addAttribute("Type", "queue");

        return "feedback/queue";
    }

    @IsBbrAdmin
    @GetMapping("/feedback/owner")
    public String ownerList(Model model) {

        List<FeedbackDao> feedbackList = this.feedbackService.fetchOwnerQueue();
        int feedbackCount = this.feedbackService.fetchFeedbackCount();
        int spamCount = this.feedbackService.fetchSpamCount();
        int inconclusiveCount = this.feedbackService.fetchInconclusiveCount();

        model.addAttribute("Feedback", feedbackList);
        model.addAttribute("FeedbackCount", feedbackCount);
        model.addAttribute("OwnerCount", feedbackList.size());
        model.addAttribute("SpamCount", spamCount);
        model.addAttribute("InconclusiveCount", inconclusiveCount);
        model.addAttribute("CurrentUserCode", this.securityService.getCurrentUsername());
        model.addAttribute("Type", "owner");

        return "feedback/queue";
    }

    @IsBbrAdmin
    @GetMapping("/feedback/inconclusive")
    public String inconclusiveList(Model model) {

        List<FeedbackDao> feedbackList = this.feedbackService.fetchInconclusive();
        int feedbackCount = this.feedbackService.fetchFeedbackCount();
        int ownerCount = this.feedbackService.fetchOwnerCount();
        int spamCount = this.feedbackService.fetchSpamCount();

        model.addAttribute("Feedback", feedbackList);
        model.addAttribute("FeedbackCount", feedbackCount);
        model.addAttribute("OwnerCount", ownerCount);
        model.addAttribute("SpamCount", spamCount);
        model.addAttribute("InconclusiveCount", feedbackList.size());
        model.addAttribute("CurrentUserCode", this.securityService.getCurrentUsername());
        model.addAttribute("Type", "inconclusive");

        return "feedback/queue";
    }

    @IsBbrAdmin
    @GetMapping("/feedback/spam")
    public String spamList(Model model) {

        List<FeedbackDao> feedbackList = this.feedbackService.fetchSpam();
        int feedbackCount = this.feedbackService.fetchFeedbackCount();
        int ownerCount = this.feedbackService.fetchOwnerCount();
        int inconclusiveCount = this.feedbackService.fetchInconclusiveCount();

        model.addAttribute("Feedback", feedbackList);
        model.addAttribute("FeedbackCount", feedbackCount);
        model.addAttribute("OwnerCount", ownerCount);
        model.addAttribute("SpamCount", feedbackList.size());
        model.addAttribute("InconclusiveCount", inconclusiveCount);
        model.addAttribute("CurrentUserCode", this.securityService.getCurrentUsername());
        model.addAttribute("Type", "spam");

        return "feedback/queue";
    }

    @IsBbrSuperuser
    @GetMapping("/feedback/detail/{id:\\d+}")
    public String feedbackDetail(Model model, @PathVariable("id") Long feedbackId) throws NotFoundException {
        Optional<FeedbackDao> feedback = this.feedbackService.fetchById(feedbackId);
        if (feedback.isEmpty()) {
            throw NotFoundException.feedbackNotFoundById(feedbackId);
        }

        model.addAttribute("Feedback", feedback.get());

        return "feedback/detail";
    }
}
