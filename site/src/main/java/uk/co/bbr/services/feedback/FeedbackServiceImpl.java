package uk.co.bbr.services.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.email.EmailService;
import uk.co.bbr.services.feedback.dao.FeedbackDao;
import uk.co.bbr.services.feedback.repo.FeedbackRepository;
import uk.co.bbr.services.feedback.types.FeedbackStatus;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SecurityService securityService;
    private final UserService userService;
    private final EmailService emailService;

    @Override
    public void submit(String url, String referrer, String ownerUsercode, String feedback, String browserName, String ip) {
        String currentUsername = this.securityService.getCurrentUsername();

        FeedbackDao newFeedback = new FeedbackDao();
        newFeedback.setUrl(url);
        newFeedback.setComment(feedback);
        newFeedback.setBrowser(browserName);
        newFeedback.setIp(ip);
        if (ownerUsercode != null && ownerUsercode.length() > 0) {
            newFeedback.setOwnedBy(ownerUsercode);
        }

        newFeedback.addAuditLog(currentUsername, "Referrer: " + url);
        newFeedback.addAuditLog(currentUsername, "Ip: " + ip);

        newFeedback.setStatus(FeedbackStatus.NEW);

        this.create(newFeedback);
    }

    @Override
    public FeedbackDao create(FeedbackDao feedback) {
        String currentUsercode = this.securityService.getCurrentUsername();
        String reportedBy = currentUsercode;
        if (currentUsercode == null || currentUsercode.strip().length() == 0 || currentUsercode.equals("anonymousUser")) {
            reportedBy = null;
        }

        feedback.addAuditLog(currentUsercode, "Reported by: " + currentUsercode);

        feedback.setReportedBy(reportedBy);

        feedback.setCreated(LocalDateTime.now());
        feedback.setCreatedBy(reportedBy);
        feedback.setUpdated(LocalDateTime.now());
        feedback.setUpdatedBy(reportedBy);

        FeedbackDao newFeedback = this.feedbackRepository.saveAndFlush(feedback);

        Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(feedback.getOwnedBy());
        if (user.isEmpty()) {
            user = this.userService.fetchUserByUsercode("tjs");
        }
        user.ifPresent(bbrUserDao -> this.emailService.sendFeedbackEmail(bbrUserDao, newFeedback.getComment(), newFeedback.getUrl()));

        return newFeedback;
    }

    @Override
    public FeedbackDao update(FeedbackDao feedback) {
        String currentUsercode = this.securityService.getCurrentUsername();

        feedback.setUpdated(LocalDateTime.now());
        feedback.setUpdatedBy(currentUsercode);

        return this.feedbackRepository.saveAndFlush(feedback);
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
