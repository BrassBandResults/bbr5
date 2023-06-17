package uk.co.bbr.services.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.feedback.repo.FeedbackRepository;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.security.SecurityService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SecurityService securityService;

    @Override
    public void submit(String url, String referrer, String ownerUsercode, String feedback, String browserName, String ip) {
        FeedbackDao newFeedback = new FeedbackDao();
        newFeedback.setUrl(url);
        newFeedback.setComment(feedback);
        newFeedback.setBrowser(browserName);
        newFeedback.setIp(ip);

        newFeedback.addAuditLog("Referrer: " + url);
        newFeedback.addAuditLog("Ip: " + ip);

        newFeedback.setStatus(FeedbackStatus.NEW);

        this.create(newFeedback);
    }

    @Override
    public void create(FeedbackDao feedback) {
        String currentUsercode = this.securityService.getCurrentUsername();
        String reportedBy = currentUsercode;
        if (currentUsercode == null || currentUsercode.trim().length() == 0 || currentUsercode.equals("anonymousUser")) {
            reportedBy = "owner";
        }

        feedback.addAuditLog("Reported by: " + currentUsercode);

        feedback.setReportedBy(reportedBy);

        feedback.setCreated(LocalDateTime.now());
        feedback.setCreatedBy(reportedBy);
        feedback.setUpdated(LocalDateTime.now());
        feedback.setUpdatedBy(reportedBy);

        this.feedbackRepository.saveAndFlush(feedback);
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

    @Override
    public List<FeedbackDao> fetchSuperuserQueue() {
        return this.feedbackRepository.fetchForType(FeedbackStatus.NEW);
    }

    @Override
    public List<FeedbackDao> fetchOwnerQueue() {
        return this.feedbackRepository.fetchForType(FeedbackStatus.OWNER);
    }

    @Override
    public List<FeedbackDao> fetchSpam() {
        return this.feedbackRepository.fetchForType(FeedbackStatus.SPAM);
    }

    @Override
    public List<FeedbackDao> fetchInconclusive() {
        return this.feedbackRepository.fetchForType(FeedbackStatus.INCONCLUSIVE);
    }

    @Override
    public int fetchFeedbackCount() {
        return this.feedbackRepository.fetchCount(FeedbackStatus.NEW);
    }

    @Override
    public int fetchOwnerCount() {
        return this.feedbackRepository.fetchCount(FeedbackStatus.OWNER);
    }

    @Override
    public int fetchSpamCount() {
        return this.feedbackRepository.fetchCount(FeedbackStatus.SPAM);
    }

    @Override
    public int fetchInconclusiveCount() {
        return this.feedbackRepository.fetchCount(FeedbackStatus.INCONCLUSIVE);
    }

    @Override
    public Optional<FeedbackDao> fetchById(Long feedbackId) {
        return this.feedbackRepository.findById(feedbackId);
    }
}
