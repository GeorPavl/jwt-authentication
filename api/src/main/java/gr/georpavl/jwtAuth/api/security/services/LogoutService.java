package gr.georpavl.jwtAuth.api.security.services;

import gr.georpavl.jwtAuth.api.domain.tokens.Token;
import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.HttpServletResponseWriterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenJpaRepository tokenJpaRepository;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final var authHeader = request.getHeader("Authorization");
    if (isAuthorizationHeaderInvalid(authHeader)) return;
    var storedToken = findTokenByJwt(authHeader);
    handleTokenAndRespond(response, storedToken);
  }

  private void handleTokenAndRespond(HttpServletResponse response, Optional<Token> storedToken) {
    if (storedToken.isPresent()) {
      invalidateToken(storedToken.get());
      buildHttpResponse(response, LogoutMessages.SUCCESS.getMessage(), HttpStatus.ACCEPTED);
    } else {
      buildHttpResponse(
          response, LogoutMessages.INVALID_TOKEN.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
  }

  private void invalidateToken(Token storedToken) {
    storedToken.setExpired(true);
    storedToken.setRevoked(true);
    tokenJpaRepository.save(storedToken);
    SecurityContextHolder.clearContext();
  }

  private Optional<Token> findTokenByJwt(String authHeader) {
    final var jwt = authHeader.substring(7);
    return tokenJpaRepository.findByValue(jwt);
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
