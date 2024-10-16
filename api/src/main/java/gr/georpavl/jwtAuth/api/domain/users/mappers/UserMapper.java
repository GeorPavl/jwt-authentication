package gr.georpavl.jwtAuth.api.domain.users.mappers;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RefreshTokenRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.CommonSecurityException;
import gr.georpavl.jwtAuth.api.security.services.JwtService;
import gr.georpavl.jwtAuth.api.utils.generators.RandomCodeGenerator;
import gr.georpavl.jwtAuth.api.utils.generators.TokenGenerator;
import gr.georpavl.jwtAuth.api.utils.generators.UUIDGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMapper {

  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public User toEntity(RegistrationRequest request) {
    return User.of(
        UUIDGenerator.generateUUID(),
        request.email(),
        request.firstName(),
        request.lastName(),
        request.phoneNumber(),
        passwordEncoder.encode(request.password()),
        request.role(),
        false,
        false,
        RandomCodeGenerator.generateRandomCode(),
        TokenGenerator.generateToken());
  }

  public User toEntity(User existingUser, UpdateUserRequest request) {
    return User.of(
        existingUser.getId(),
        request.email(),
        request.firstName(),
        request.lastName(),
        request.phoneNumber(),
        existingUser.getPassword(),
        existingUser.getRole(),
        existingUser.getCreatedAt(),
        existingUser.isEnabled(),
        existingUser.isVerified(),
        existingUser.getVerifiedAt(),
        existingUser.getUpdatedAt(),
        existingUser.getToken(),
        existingUser.getCode());
  }

  public UserResponse toResponse(User user) {
    return UserResponse.of(user);
  }

  public RefreshTokenRequest toRefreshTokenRequest(HttpServletRequest servletRequest) {
    final var authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw CommonSecurityException.headerError();
    }

    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);

    if (userEmail == null || userEmail.isBlank()) {
      throw new UsernameNotFoundException("User not found");
    }

    return RefreshTokenRequest.of(userEmail, refreshToken);
  }
}
