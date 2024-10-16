package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.users.User;
import jakarta.servlet.http.HttpServletRequest;

public interface TokenManagerService {
  AuthenticationResponse refreshToken(HttpServletRequest request);

  String createAndSaveToken(User user, String tokenType);
}
