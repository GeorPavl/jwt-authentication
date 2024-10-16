package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RefreshTokenRequest;
import gr.georpavl.jwtAuth.api.domain.tokens.services.TokenService;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.CommonSecurityException;
import gr.georpavl.jwtAuth.api.security.services.JwtService;
import gr.georpavl.jwtAuth.api.security.userDetails.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenManagerServiceImpl implements TokenManagerService {

  private final UserMapper userMapper;
  private final UserJpaRepository userJpaRepository;
  private final TokenService tokenService;
  private final JwtService jwtService;

  @Override
  public AuthenticationResponse refreshToken(HttpServletRequest servletRequest) {
    var request = userMapper.toRefreshTokenRequest(servletRequest);
    var user = findUserOrElseThrow(request.email());
    validateTokenOrElseThrow(request, user);
    tokenService.revokeUsersTokens(user.getId());
    return generateTokensAndReturnAuthenticationResponse(user);
  }

  @Override
  public String createAndSaveToken(User user, String tokenType) {
    var token =
        (tokenType.equals("ACCESS"))
            ? jwtService.generateToken(new UserDetailsImpl(user))
            : jwtService.generateRefreshToken(new UserDetailsImpl(user));
    return tokenService.createToken(user, token).getValue();
  }

  private void validateTokenOrElseThrow(RefreshTokenRequest request, User user) {
    if (!jwtService.isTokenValid(request.refreshToken(), new UserDetailsImpl(user))) {
      throw CommonSecurityException.headerError();
    }
  }

  private AuthenticationResponse generateTokensAndReturnAuthenticationResponse(User user) {
    var accessToken = createAndSaveToken(user, "ACCESS");
    var refreshToken = createAndSaveToken(user, "REFRESH");
    return AuthenticationResponse.of(UserResponse.of(user), accessToken, refreshToken);
  }

  private User findUserOrElseThrow(String email) {
    return userJpaRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User %s not found", email)));
  }
}
