package gr.georpavl.jwtAuth.api.domain.authentication.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import gr.georpavl.jwtAuth.api.domain.authentication.AuthenticationFixture;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.utils.mailService.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class RegistrationServiceImplTest {

  @InjectMocks private RegistrationServiceImpl registrationService;
  @Mock private UserJpaRepository userJpaRepository;
  @Mock private UserMapper userMapper;
  @Mock private AuthorizationTokensManagementService authorizationTokensManagementService;
  @Mock private MailService mailService;

  private static final RegistrationRequest REGISTRATION_REQUEST =
      AuthenticationFixture.createRegistrationRequest();

  @Test
  void register_shouldRegisterUserSuccessfully() {
    log.warn("request {}", REGISTRATION_REQUEST);
    doNothing().when(mailService).sendVerificationEmail(any(), any(), any());
    when(userJpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(authorizationTokensManagementService.generateTokensAndReturnAuthenticationResponse(any()))
        .thenReturn(AuthenticationFixture.createAuthenticationResponse());
    when(userMapper.toEntity(any())).thenReturn(UserFixture.createTestUser());

    var result = registrationService.register(REGISTRATION_REQUEST);
    log.warn("result {}", result);

    //    assertEquals(REGISTRATION_REQUEST.email(), result.userResponse().email());
  }
}
