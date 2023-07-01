package uk.co.bbr.services.email;

public interface EmailService {
    void sendActivationEmail(String email, String activationKey);
}
