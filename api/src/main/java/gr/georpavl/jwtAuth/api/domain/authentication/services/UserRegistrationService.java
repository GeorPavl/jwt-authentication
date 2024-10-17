package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.services.UserService;
import gr.georpavl.jwtAuth.api.security.exceptions.SecurityExceptionFactory;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UserAlreadyRegisteredException;
import gr.georpavl.jwtAuth.api.utils.exceptions.SqlExceptionUtilsFactory;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.PasswordMissMatchException;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceAlreadyPresentException;
import gr.georpavl.jwtAuth.api.utils.mailService.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

  private final UserService userService;
  private final UserMapper userMapper;
  private final TokenManagerService tokenManagerService;
  private final MailService mailService;

  public AuthenticationResponse register(RegistrationRequest request) {
    try {
      checkIfConfirmationPasswordMatches(request);
      var user = createUser(request);
      sendVerificationEmail(user);
      return tokenManagerService.generateTokensAndReturnAuthenticationResponse(user);
    } catch (RuntimeException e) {
      throw handleRegistrationException(request.email(), e);
    }
  }

  private void checkIfConfirmationPasswordMatches(RegistrationRequest request) {
    if (!request.password().equals(request.confirmationPassword())) {
      throw new PasswordMissMatchException();
    }
  }

  private User createUser(RegistrationRequest request) {
    return userService.createUser(userMapper.toEntity(request));
  }

  private void sendVerificationEmail(User user) {
    mailService.sendVerificationEmail(user.getEmail(), user.getToken(), user.getCode());
  }

  private RuntimeException handleRegistrationException(String email, RuntimeException e) {
    if (e instanceof DataIntegrityViolationException) {
      var translatedException = SqlExceptionUtilsFactory.of((DataIntegrityViolationException) e);
      if (translatedException instanceof ResourceAlreadyPresentException) {
        return new UserAlreadyRegisteredException(email);
      }
      return translatedException;
    }

    return SecurityExceptionFactory.handleSecurityException(e);
  }
}
