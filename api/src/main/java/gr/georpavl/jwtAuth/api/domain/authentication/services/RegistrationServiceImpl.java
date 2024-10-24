package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.handlers.SecurityExceptionFactory;
import gr.georpavl.jwtAuth.api.utils.exceptions.handlers.SqlExceptionUtilsFactory;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.PasswordMissMatchException;
import gr.georpavl.jwtAuth.api.utils.mailService.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

  private final UserJpaRepository userJpaRepository;
  private final UserMapper userMapper;
  private final AuthorizationTokensManagementService authorizationTokensManagementService;
  private final MailService mailService;

  @Override
  public AuthenticationResponse register(RegistrationRequest request) {
    try {
      checkIfConfirmationPasswordMatches(request);
      var user = createUser(request);
      sendVerificationEmail(user);
      return authorizationTokensManagementService.generateTokensAndReturnAuthenticationResponse(
          user);
    } catch (RuntimeException e) {
      throw handleRegistrationException(e);
    }
  }

  private void checkIfConfirmationPasswordMatches(RegistrationRequest request) {
    if (!request.password().equals(request.confirmationPassword())) {
      throw new PasswordMissMatchException();
    }
  }

  private User createUser(RegistrationRequest request) {
    return userJpaRepository.save(userMapper.toEntity(request));
  }

  private void sendVerificationEmail(User user) {
    mailService.sendVerificationEmail(user.getEmail(), user.getToken(), user.getCode());
  }

  private RuntimeException handleRegistrationException(RuntimeException e) {
    if (e instanceof DataIntegrityViolationException) {
      return SqlExceptionUtilsFactory.handle((DataIntegrityViolationException) e);
    }
    return SecurityExceptionFactory.handleSecurityException(e);
  }
}
