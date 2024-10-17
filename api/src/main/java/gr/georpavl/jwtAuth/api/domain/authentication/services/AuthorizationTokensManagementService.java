package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.users.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthorizationTokensManagementService {
  AuthenticationResponse refreshToken(HttpServletRequest request);

  String createAndSaveToken(User user, String tokenType);

  AuthenticationResponse generateTokensAndReturnAuthenticationResponse(User user);
}
