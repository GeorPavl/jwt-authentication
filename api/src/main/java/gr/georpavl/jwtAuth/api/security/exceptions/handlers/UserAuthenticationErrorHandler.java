package gr.georpavl.jwtAuth.api.security.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.NoPermissionException;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Slf4j
public class UserAuthenticationErrorHandler extends BasicAuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  public UserAuthenticationErrorHandler() {
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
      throws IOException {
    response.setContentType("application/json");
    RuntimeException translatedException = SecurityExceptionFactory.handleSecurityException(ex);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    if (translatedException instanceof UnauthorizedAccessException) {
      log.error("Access denied error: {}", ex.getMessage(), ex);
    } else if (translatedException instanceof NoPermissionException) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      log.error("Authentication error: {}", ex.getMessage(), ex);
    } else {
      log.error("Unknown security exception: {}", ex.getMessage(), ex);
    }

    String jsonErrorMessage =
        this.objectMapper.writeValueAsString(
            Map.of("errors", Map.of("error", List.of(translatedException.getMessage()))));

    response.getWriter().println(jsonErrorMessage);
  }
}
