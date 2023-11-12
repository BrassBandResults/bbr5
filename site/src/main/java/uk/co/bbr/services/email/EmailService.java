package uk.co.bbr.services.email;

import uk.co.bbr.services.security.dao.SiteUserDao;

public interface EmailService {
    void sendActivationEmail(String email, String activationKey);

    void sendFeedbackEmail(SiteUserDao user, String feedbackComment, String feedbackOffset, String submitter);

    void sendResetPasswordEmail(SiteUserDao siteUserDao);
}
