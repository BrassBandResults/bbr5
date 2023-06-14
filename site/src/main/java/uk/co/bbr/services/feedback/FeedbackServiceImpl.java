package uk.co.bbr.services.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.feedback.repo.FeedbackRepository;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.security.SecurityService;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SecurityService securityService;

    @Override
    public void submit(String url, String referrer, String ownerUsercode, String feedback, String browserName, String ip) {

        String currentUsercode = this.securityService.getCurrentUsername();
        String reportedBy = currentUsercode;
        if (currentUsercode == null || currentUsercode.trim().length() == 0 || currentUsercode.equals("anonymousUser")) {
            reportedBy = "owner";
        }

        FeedbackDao newFeedback = new FeedbackDao();
        newFeedback.setUrl(url);
        newFeedback.setReportedBy(reportedBy);
        newFeedback.setComment(feedback);
        newFeedback.setBrowser(browserName);
        newFeedback.setIp(ip);

        newFeedback.addAuditLog("Referrer: " + url);
        newFeedback.addAuditLog("Reported by: " + currentUsercode);
        newFeedback.addAuditLog("Ip: " + ip);

        newFeedback.setStatus(FeedbackStatus.NEW);

        newFeedback.setCreated(LocalDateTime.now());
        newFeedback.setCreatedBy(reportedBy);
        newFeedback.setUpdated(LocalDateTime.now());
        newFeedback.setUpdatedBy(reportedBy);

        this.feedbackRepository.saveAndFlush(newFeedback);
    }

    @Override
    public Optional<FeedbackDao> fetchLatestFeedback(String offset) {
        List<FeedbackDao> feedbackForOffset = this.feedbackRepository.fetchFeedbackForOffset(offset);
        if (!feedbackForOffset.isEmpty()) {
            return Optional.of(feedbackForOffset.get(0));
        } else {
            return Optional.empty();
        }
    }
}
