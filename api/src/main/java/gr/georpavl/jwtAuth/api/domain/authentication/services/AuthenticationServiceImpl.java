package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RefreshTokenRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.tokens.services.TokenService;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.services.UserService;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.CommonSecurityException;
import gr.georpavl.jwtAuth.api.security.exceptions.UserAlreadyRegisteredException;
import gr.georpavl.jwtAuth.api.security.services.JwtService;
import gr.georpavl.jwtAuth.api.security.userDetails.UserDetailsImpl;
import gr.georpavl.jwtAuth.api.utils.exceptions.ExceptionUtilsFactory;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceAlreadyPresentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserJpaRepository userJpaRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;
  private final UserMapper userMapper;
  private final UserService userService;

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

  // FIXME: 15/10/2024 Validations, exception handling
  @Override
  public AuthenticationResponse register(RegistrationRequest request) {
    var user = userMapper.toEntity(request);
    try {
      var registeredUser = userService.createUser(user);
      return generateTokensAndReturnAuthenticationResponse(registeredUser);
    } catch (DataIntegrityViolationException e) {
      var translatedException = ExceptionUtilsFactory.of(e);
      if (translatedException instanceof ResourceAlreadyPresentException) {
        throw new UserAlreadyRegisteredException(request.email());
      }
      throw translatedException;
    }
    catch (Exception e) {
      log.error("Error during authentication process for user {}", request.email(), e);
      throw e;
    }
  }

  @Override
  public AuthenticationResponse refreshToken(HttpServletRequest servletRequest) {
    var request = userMapper.toRefreshTokenRequest(servletRequest);
    var user = findUserOrElseThrow(request.email());
    validateTokenOrElseThrow(request, user);
    try {
      tokenService.revokeUsersTokens(user.getId());
      return generateTokensAndReturnAuthenticationResponse(user);
    } catch (Exception e) {
      log.error("Error during token refreshing process for user", e);
      throw e;
    }
  }

  private AuthenticationResponse generateTokensAndReturnAuthenticationResponse(User user) {
    var accessToken = createAndSaveAccessToken(user);
    var refreshToken = createAndSaveRefreshToken(user);
    return AuthenticationResponse.of(UserResponse.of(user), accessToken, refreshToken);
  }

  private String createAndSaveAccessToken(User user) {
    var accessToken = jwtService.generateToken(new UserDetailsImpl(user));
    tokenService.createToken(user, accessToken);
    return accessToken;
  }

  private String createAndSaveRefreshToken(User user) {
    var refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));
    tokenService.createToken(user, refreshToken);
    return refreshToken;
  }

  private void authenticateCredentials(String email, String password) {
    // TODO: 15/10/2024 Check the response of the exception
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
  }

  private User findUserOrElseThrow(String email) {
    return userJpaRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User %s not found", email)));
  }

  private void validateTokenOrElseThrow(RefreshTokenRequest request, User user) {
    if (!jwtService.isTokenValid(request.refreshToken(), new UserDetailsImpl(user))) {
      throw CommonSecurityException.headerError();
    }
  }
}
