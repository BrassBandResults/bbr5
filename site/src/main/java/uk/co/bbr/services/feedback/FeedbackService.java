package uk.co.bbr.services.feedback;

import uk.co.bbr.services.feedback.dao.FeedbackDao;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    void submit(String url, String referrer, String ownerUsercode, String feedback, String browserName, String ip);

    Optional<FeedbackDao> fetchLatestFeedback(String offset);

    List<FeedbackDao> fetchSuperuserQueue();

    List<FeedbackDao> fetchOwnerQueue();

    List<FeedbackDao> fetchSpam();

    List<FeedbackDao> fetchInconclusive();

    int fetchFeedbackCount();

    int fetchOwnerCount();

    int fetchSpamCount();

    int fetchInconclusiveCount();

    Optional<FeedbackDao> fetchById(Long feedbackId);

    void create(FeedbackDao feedback);

    void update(FeedbackDao feedback);
}
