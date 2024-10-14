package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
  AuthenticationResponse authenticate(AuthenticationRequest request);

  AuthenticationResponse register(RegistrationRequest request);

  AuthenticationResponse refreshToken(HttpServletRequest request);
}
