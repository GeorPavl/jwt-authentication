package gr.georpavl.jwtAuth.api.security.services;

import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
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

  private final TokenJpaRepository tokenJpaRepository;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final var authHeader = request.getHeader("Authorization");
    final String jwt;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }

    jwt = authHeader.substring(7);

    var storedToken = tokenJpaRepository.findByValue(jwt).orElse(null);

    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenJpaRepository.save(storedToken);
      SecurityContextHolder.clearContext();
      // Set response if success
      var message = "Logout successful";
      response.setStatus(HttpStatus.ACCEPTED.value());
      response.setContentType("text/plain"); // Set content type as needed
      try {
        response.getWriter().write(message);
      } catch (IOException e) {
        throw new HttpServletResponseWriterException(e.getMessage());
      }
    } else {
      // Set a different message in the response
      var errorMessage = "Invalid token or user";
      response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
      response.setContentType("text/plain"); // Set content type as needed

      try {
        response.getWriter().write(errorMessage);
      } catch (IOException e) {
        throw new HttpServletResponseWriterException(e.getMessage());
      }
    }
  }
}
