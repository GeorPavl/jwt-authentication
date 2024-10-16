package gr.georpavl.jwtAuth.api.security.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    log.error("Authentication error: {}", ex.getMessage(), ex);
    var errorMessage =
        "The access token provided has expired, revoked, erroneous, or is otherwise invalid.";

    var jsonErrorMessage =
        this.objectMapper.writeValueAsString(
            Map.of("errors", Map.of("UNAUTHORIZED:", List.of(errorMessage))));

    final PrintWriter writer = response.getWriter();
    writer.println(jsonErrorMessage);
  }
}
