package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.ChangePasswordRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
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
  private final AuthorizationTokensManagementService authorizationTokensManagementService;
  private final ChangePasswordService changePasswordService;

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

  @Override
  public AuthenticationResponse refreshToken(HttpServletRequest request) {
    return authorizationTokensManagementService.refreshToken(request);
  }

  @Override
  public void changePassword(ChangePasswordRequest request) {
    changePasswordService.changePassword(request);
  }
}
