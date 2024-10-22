package gr.georpavl.jwtAuth.api.security.services;

import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.HttpServletResponseWriterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final var authHeader = request.getHeader("Authorization");
    if (isAuthorizationHeaderInvalid(authHeader))
      buildHttpResponse(response, LogoutMessages.INVALID_TOKEN.getMessage(), HttpStatus.CONFLICT);
    invalidateToken();
    buildHttpResponse(response, LogoutMessages.SUCCESS.getMessage(), HttpStatus.ACCEPTED);
  }

  private void invalidateToken() {
    SecurityContextHolder.clearContext();
  }

  private static boolean isAuthorizationHeaderInvalid(String authHeader) {
    return authHeader == null || !authHeader.startsWith("Bearer ");
  }

  private void buildHttpResponse(HttpServletResponse response, String message, HttpStatus status) {
    response.setContentType("text/plain");
    response.setStatus(status.value());
    writeResponseMessage(response, message);
  }

  private static void writeResponseMessage(HttpServletResponse response, String message) {
    try {
      response.getWriter().write(message);
    } catch (IOException e) {
      throw new HttpServletResponseWriterException(e.getMessage());
    }
  }
}
