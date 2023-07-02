package uk.co.bbr.services.email;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String EMAIL_FROM_ACCOUNTS = "accounts@brassbandresults.co.uk";

    private final MessageSource messageSource;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendActivationEmail(String destinationEmail, String activationKey) {
        StringBuilder messageText = new StringBuilder();
        messageText.append(this.messageSource.getMessage("email.account-activation.line1", null, LocaleContextHolder.getLocale())).append("\n\n");
        messageText.append(this.messageSource.getMessage("email.account-activation.line2", null, LocaleContextHolder.getLocale())).append("\n\n");
        messageText.append("https://brassbandresults.co.uk/acc/activate/").append(activationKey).append("\n\n");
        messageText.append(this.messageSource.getMessage("email.salutation", null, LocaleContextHolder.getLocale()));

        this.sendEmail(EmailServiceImpl.EMAIL_FROM_ACCOUNTS, destinationEmail, "email.account-activation.subject", messageText.toString());
    }

    private void sendEmail(String fromEmail, String toEmail, String subjectTranslationKey, String contents) {
        List<String> toEmails = new ArrayList<>();
        toEmails.add(toEmail);

        this.sendEmail(fromEmail, toEmails, subjectTranslationKey, contents);
    }

    private void sendEmail(String fromEmail, List<String> to, String subjectTranslationKey, String contents) {
        MimeMessage message = javaMailSender.createMimeMessage();
        String subject = this.messageSource.getMessage(subjectTranslationKey, null, LocaleContextHolder.getLocale());

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(to.parallelStream().toArray(String[]::new));
            helper.setBcc("bcc@brassbandresults.co.uk");
            helper.setText(contents);
            helper.setSubject(subject);
            helper.setFrom(new InternetAddress(fromEmail, "BrassBandResults"));
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        javaMailSender.send(message);
    }
}
