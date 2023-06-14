package uk.co.bbr.services.feedback;

import uk.co.bbr.services.feedback.dao.FeedbackDao;

import java.util.Optional;

public interface FeedbackService {
    void submit(String url, String referrer, String ownerUsercode, String feedback, String browserName, String ip);

    Optional<FeedbackDao> fetchLatestFeedback(String offset);
}
