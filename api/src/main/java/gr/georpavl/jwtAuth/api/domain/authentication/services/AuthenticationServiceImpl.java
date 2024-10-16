package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.tokens.services.TokenService;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.domain.users.services.UserService;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UserAlreadyRegisteredException;
import gr.georpavl.jwtAuth.api.security.services.JwtService;
import gr.georpavl.jwtAuth.api.utils.exceptions.ExceptionUtilsFactory;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.PasswordMissMatchException;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceAlreadyPresentException;
import gr.georpavl.jwtAuth.api.utils.mailService.MailService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserJpaRepository userJpaRepository;
  private final JwtService jwtService;
  private final TokenService tokenService;
  private final UserMapper userMapper;
  private final UserService userService;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final MailService mailService;
  private final TokenManagerService tokenManagerService;

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticateCredentials(request.email(), request.password());
    var user = findUserOrElseThrow(request.email());
    try {
      tokenService.revokeUsersTokens(user.getId());
      return generateTokensAndReturnAuthenticationResponse(user);
    } catch (Exception e) {
      log.error("Error during authentication process for user {}", request.email(), e);
      throw e;
    }
  }

  @Override
  public AuthenticationResponse register(RegistrationRequest request) {
    checkIfConfirmationPasswordMatches(request);
    var user = createUser(request);
    sendVerificationEmail(user);
    return generateTokensAndReturnAuthenticationResponse(user);
  }

  @Override
  public void verify(String token, Integer code) {
    var user = getUserIfSessionNotExpired(code);
    isVerificationTokenValid(token, user);
    enableAndVerifyUserAccount(user);
  }

  /**
   * Authenticates the user's credentials using an AuthenticationManager built with
   * AuthenticationManagerBuilder. This approach is chosen over injecting an AuthenticationManager
   * bean directly to avoid potential circular dependencies and to allow for dynamic creation of the
   * AuthenticationManager when needed.
   *
   * @throws AuthenticationException if authentication fails
   */
  private void authenticateCredentials(String email, String password) {
    try {
      AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
      var authentication = new UsernamePasswordAuthenticationToken(email, password);
      authenticationManager.authenticate(authentication);
    } catch (AuthenticationException e) {
      log.error("Authentication failed for user {}", email, e);
      throw e;
    }
  }

  private User findUserOrElseThrow(String email) {
    return userJpaRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User %s not found", email)));
  }

  private AuthenticationResponse generateTokensAndReturnAuthenticationResponse(User user) {
    var accessToken = tokenManagerService.createAndSaveToken(user, "ACCESS");
    var refreshToken = tokenManagerService.createAndSaveToken(user, "REFRESH");
    return AuthenticationResponse.of(UserResponse.of(user), accessToken, refreshToken);
  }

  private User createUser(RegistrationRequest request) {
    try {
      return userService.createUser(userMapper.toEntity(request));
    } catch (DataIntegrityViolationException e) {
      handleRegistrationException(request.email(), e);
      return null;
    }
  }

  private void handleRegistrationException(String email, DataIntegrityViolationException e) {
    var translatedException = ExceptionUtilsFactory.of(e);
    if (translatedException instanceof ResourceAlreadyPresentException) {
      throw new UserAlreadyRegisteredException(email);
    }
    throw translatedException;
  }

  private void sendVerificationEmail(User user) {
    mailService.sendVerificationEmail(user.getEmail(), user.getToken(), user.getCode());
  }

  private void checkIfConfirmationPasswordMatches(RegistrationRequest request) {
    if (!request.password().equals(request.confirmationPassword())) {
      throw new PasswordMissMatchException();
    }
  }

  private User getUserIfSessionNotExpired(Integer code) {
    return userJpaRepository
        .findByCode(code)
        .orElseThrow(() -> new SessionAuthenticationException("SESSION_EXPIRED"));
  }

  private static void isVerificationTokenValid(String token, User user) {
    if (!user.getToken().equals(token)) {
      throw new SessionAuthenticationException("INVALID_TOKEN");
    }
  }

  private void enableAndVerifyUserAccount(User user) {
    try {
      user.setEnabled(true);
      user.setVerified(true);
      user.setVerifiedAt(LocalDateTime.now());
      user.setCode(null);
      user.setToken(null);
      userJpaRepository.save(user);
    } catch (Exception e) {
      throw new SessionAuthenticationException("Error saving user verification details.");
    }
  }
}
