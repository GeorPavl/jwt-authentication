package gr.georpavl.jwtAuth.api.domain.authentication.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.georpavl.jwtAuth.api.domain.authentication.AuthenticationFixture;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UnauthorizedAccessException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

  @InjectMocks private LoginServiceImpl loginService;

  @Mock private UserJpaRepository userJpaRepository;

  @Mock private AuthorizationTokensManagementService authorizationTokensManagementService;

  @Mock private AuthenticationManagerBuilder authenticationManagerBuilder;

  @Mock private AuthenticationManager authenticationManager;

  @BeforeEach
  void setUp() {
    when(authenticationManagerBuilder.getOrBuild()).thenReturn(authenticationManager);
  }

  @Test
  void login_shouldAuthenticateUserSuccessfully() {
    var request = AuthenticationFixture.createAuthenticationRequest();
    var user = new User();
    var expectedResponse = AuthenticationFixture.createAuthenticationResponse();

    when(userJpaRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
    when(authorizationTokensManagementService.generateTokensAndReturnAuthenticationResponse(user))
        .thenReturn(expectedResponse);

    var result = loginService.login(request);

    assertEquals(expectedResponse, result);
    verify(authenticationManager).authenticate(any(Authentication.class));
    verify(userJpaRepository).findByEmail(request.email());
    verify(authorizationTokensManagementService)
        .generateTokensAndReturnAuthenticationResponse(user);
  }

  @Test
  void login_shouldThrowExceptionWhenUserNotFound() {
    var request = AuthenticationFixture.createAuthenticationRequest();

    when(userJpaRepository.findByEmail(request.email())).thenReturn(Optional.empty());

    assertThrows(UnauthorizedAccessException.class, () -> loginService.login(request));
    verify(userJpaRepository).findByEmail(request.email());
  }

  @Test
  void login_shouldThrowExceptionWhenInvalidCredentials() {
    var request = AuthenticationFixture.createAuthenticationRequest();

    doThrow(new BadCredentialsException("Invalid credentials"))
        .when(authenticationManager)
        .authenticate(any(Authentication.class));

    assertThrows(UnauthorizedAccessException.class, () -> loginService.login(request));
    verify(userJpaRepository, times(0)).findByEmail(request.email());
    verify(authorizationTokensManagementService, times(0))
        .generateTokensAndReturnAuthenticationResponse(any());
  }
}
