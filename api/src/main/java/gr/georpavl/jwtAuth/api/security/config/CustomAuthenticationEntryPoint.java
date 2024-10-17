package gr.georpavl.jwtAuth.api.security.config;

import gr.georpavl.jwtAuth.api.security.exceptions.handlers.UserAuthenticationErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class CustomAuthenticationEntryPoint {

  /**
   * Defines a custom AuthenticationEntryPoint to handle unauthorized access attempts. Sets a realm
   * name for unauthorized responses, which provides additional context to clients about the
   * authentication method being used (in this case, JWT-based). This configuration helps ensure
   * that unauthorized users receive a clear, standardized response, facilitating client-side error
   * handling and improving security transparency.
   *
   * @return A configured UserAuthenticationErrorHandler instance
   */
  @Bean
  public AuthenticationEntryPoint userAuthenticationErrorHandler() {
    var userAuthenticationErrorHandler = new UserAuthenticationErrorHandler();
    userAuthenticationErrorHandler.setRealmName("JWT Authentication");
    return userAuthenticationErrorHandler;
  }
}
