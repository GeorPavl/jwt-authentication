package gr.georpavl.jwtAuth.api.security.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    var translatedException = SecurityExceptionFactory.handle(accessDeniedException);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    log.error("Authentication error: {}", translatedException.getMessage(), translatedException);

    var jsonErrorMessage =
        this.objectMapper.writeValueAsString(
            Map.of("errors", Map.of("error", List.of(translatedException.getMessage()))));

    response.getWriter().println(jsonErrorMessage);
  }
}
