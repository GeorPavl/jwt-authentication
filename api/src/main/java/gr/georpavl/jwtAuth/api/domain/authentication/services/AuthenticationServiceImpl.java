package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final RegistrationService registrationService;
  private final VerificationService verificationService;
  private final LoginService loginService;

  @Override
  public AuthenticationResponse login(AuthenticationRequest request) {
    return loginService.login(request);
  }

  @Override
  public AuthenticationResponse register(RegistrationRequest request) {
    return registrationService.register(request);
  }

  @Override
  public void verify(String token, Integer code) {
    verificationService.verify(token, code);
  }

  @Override
  public void resendVerificationEmail(String userEmail) {
    verificationService.resendVerificationEmail(userEmail);
  }
}
