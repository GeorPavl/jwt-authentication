package gr.georpavl.jwtAuth.api.domain.authentication.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import gr.georpavl.jwtAuth.api.domain.authentication.AuthenticationFixture;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthenticationServiceImplTest {

  public static final AuthenticationResponse AUTHENTICATION_RESPONSE =
      AuthenticationFixture.createAuthenticationResponse();
  @InjectMocks private AuthenticationServiceImpl authenticationService;
  @Mock private RegistrationService registrationService;
  @Mock private VerificationService verificationService;
  @Mock private LoginService loginService;
  @Mock private AuthorizationTokensManagementService authorizationTokensManagementService;

  private static final AuthenticationRequest AUTHENTICATION_REQUEST =
      AuthenticationFixture.createAuthenticationRequest();

  @Test
  void login_shouldLoginUserSuccessfully() {
    when(loginService.login(AUTHENTICATION_REQUEST)).thenReturn(AUTHENTICATION_RESPONSE);

    var result = authenticationService.login(AUTHENTICATION_REQUEST);

    assertEquals(UserFixture.createTestUserResponse(), result.userResponse());
    assertNotNull(result.accessToken());
    assertNotNull(result.refreshToken());
    Mockito.verify(loginService, times(1)).login(any(AuthenticationRequest.class));
  }
}
