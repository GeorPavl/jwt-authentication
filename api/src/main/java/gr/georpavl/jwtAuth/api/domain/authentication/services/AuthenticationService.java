package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
  AuthenticationResponse login(AuthenticationRequest request);

  AuthenticationResponse register(RegistrationRequest request);

  void verify(String token, Integer code);

  void resendVerificationEmail(String userEmail);

  AuthenticationResponse refreshToken(HttpServletRequest request);
}
