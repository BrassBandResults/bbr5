package uk.co.bbr.services.email;

import uk.co.bbr.services.security.dao.BbrUserDao;

public interface EmailService {
    void sendActivationEmail(String email, String activationKey);

    void sendFeedbackEmail(BbrUserDao user, String feedbackComment, String feedbackOffset);
}
