package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.tokens.services.TokenService;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.handlers.SecurityExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

  private final UserJpaRepository userJpaRepository;
  private final TokenService tokenService;
  private final AuthorizationTokensManagementService authorizationTokensManagementService;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  /**
   * Authenticates the user's credentials using an AuthenticationManager built with
   * AuthenticationManagerBuilder. This approach is chosen over injecting an AuthenticationManager
   * bean directly to avoid potential circular dependencies and to allow for dynamic creation of the
   * AuthenticationManager when needed.
   *
   * @throws AuthenticationException if authentication fails
   */
  @Override
  public AuthenticationResponse login(AuthenticationRequest request) {
    try {
      authenticateCredentials(request.email(), request.password());
      var user = findUserOrElseThrow(request.email());
      tokenService.revokeUsersTokens(user.getId());
      return authorizationTokensManagementService.generateTokensAndReturnAuthenticationResponse(
          user);
    } catch (Exception e) {
      throw SecurityExceptionFactory.handleSecurityException(e);
    }
  }

  private void authenticateCredentials(String email, String password) {
    var authenticationManager = authenticationManagerBuilder.getOrBuild();
    var authentication = new UsernamePasswordAuthenticationToken(email, password);
    authenticationManager.authenticate(authentication);
  }

  private User findUserOrElseThrow(String email) {
    return userJpaRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User %s not found", email)));
  }
}
