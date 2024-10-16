package gr.georpavl.jwtAuth.api.utils.mailService;

import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

  private final MailConfiguration mailConfiguration;
  private final VerificationEmailHtml verificationEmailHtml;
  private static final String VERIFICATION_EMAIL_TITLE = "Verification Email";

  public void sendVerificationEmail(String toMail, String token, Integer code) {
    var emailBody = verificationEmailHtml.htmlEmailTemplate(token, String.valueOf(code));
    sendEmail(getHostUsername(), toMail, getVerificationEmailTitle(), emailBody);
  }

  public void sendEmail(String from, String to, String subject, String body) {
    try {
      var mailSender = getMailSender();
      var message = createMimeMessage(mailSender, from, to, subject, body);
      mailSender.send(message);
    } catch (Exception e) {
      throw new EmailSendingException("Failed to send email", e);
    }
  }

  private JavaMailSender getMailSender() {
    return mailConfiguration.getMailConfig();
  }

  private MimeMessage createMimeMessage(
      JavaMailSender mailSender, String from, String to, String subject, String body) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
      messageHelper.setFrom(from);
      messageHelper.setTo(to);
      messageHelper.setSubject(subject);
      messageHelper.setText(body, true);
      return message;
    } catch (MessagingException e) {
      throw new EmailSendingException("Failed to create message", e);
    }
  }

  public String getHostUsername() {
    return mailConfiguration.getUsername();
  }

  public String getVerificationEmailTitle() {
    return VERIFICATION_EMAIL_TITLE;
  }
}
