package gr.georpavl.jwtAuth.api.utils.mailService;

import java.util.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Slf4j
public class MailConfiguration {

  @Value("${mail.transport.protocol}")
  private String protocol;

  @Value("${mail.smtp.auth}")
  private String smtpAuth;

  @Value("${mail.smtp.starttls.enable}")
  private String starttls;

  @Value("${mail.debug}")
  private String debug;

  @Value("${mail.host}")
  private String host;

  @Value("${mail.port}")
  private Integer port;

  @Value("${mail.username}")
  @Getter
  private String username;

  @Value("${mail.password}")
  private String password;

  public JavaMailSenderImpl getMailConfig() {
    JavaMailSenderImpl emailConfig = new JavaMailSenderImpl();

    Properties props = emailConfig.getJavaMailProperties();
    props.put("mail.transport.protocol", protocol);
    props.put("mail.smtp.auth", smtpAuth);
    props.put("mail.smtp.starttls.enable", starttls);
    props.put("mail.debug", debug);

    emailConfig.setHost(host);
    emailConfig.setPort(port);
    emailConfig.setUsername(username);
    emailConfig.setPassword(password);

    return emailConfig;
  }
}
