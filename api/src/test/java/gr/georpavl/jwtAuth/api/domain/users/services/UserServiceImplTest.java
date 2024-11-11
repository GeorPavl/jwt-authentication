package gr.georpavl.jwtAuth.api.domain.users.services;

import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.UPDATED_EMAIL_EXAMPLE_COM;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.UPDATED_FIRST_NAME;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.UPDATED_LAST_NAME;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.UPDATED_PHONE_NUMBER;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.UUID_DEADBEEF;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.createTestUserResponse;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.createUpdateRequest;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.NoPermissionException;
import gr.georpavl.jwtAuth.api.security.services.AuthenticatedUserUtilService;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserServiceImplTest {

  @InjectMocks private UserServiceImpl userService;
  @Mock private UserJpaRepository userJpaRepository;
  @Mock private UserMapper userMapper;
  @Mock private AuthenticatedUserUtilService authenticatedUserUtilService;

  public static final UpdateUserRequest UPDATE_USER_REQUEST = UserFixture.createUpdateRequest();
  public static final User TEST_USER = UserFixture.createTestUser();
  public static final User UPDATED_USER = UserFixture.createUpdatedUser();

  @Test
  void getAllUsers_shouldFetchAllUsersSuccessfully() {
    when(userJpaRepository.findAll()).thenReturn(List.of(TEST_USER));

    var result = userService.getAllUsers();

    assertEquals(1, result.size());
    verify(userJpaRepository, times(1)).findAll();
    verify(userMapper, times(result.size())).toResponse(any(User.class));
  }

  @Test
  void getAllUsers_shouldReturnEmptyListWhenNoEntries() {
    when(userJpaRepository.findAll()).thenReturn(emptyList());

    var result = userService.getAllUsers();

    assertEquals(0, result.size());
    verify(userJpaRepository, times(1)).findAll();
    verify(userMapper, times(result.size())).toResponse(any(User.class));
  }

  @Test
  void getUserById_shouldFetchUserByIdSuccessfully() {
    when(userJpaRepository.findById(UUID_DEADBEEF)).thenReturn(Optional.of(TEST_USER));
    when(userMapper.toResponse(TEST_USER)).thenReturn(createTestUserResponse());

    var result = userService.getUserById(UUID_DEADBEEF);

    assertEquals(UUID_DEADBEEF, result.id());
    verify(userJpaRepository, times(1)).findById(UUID_DEADBEEF);
    verify(userMapper, times(1)).toResponse(any(User.class));
  }

  @Test
  void getUserById_shouldThrowExceptionWhenIdNotFound() {
    when(userJpaRepository.findById(UUID_DEADBEEF)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(UUID_DEADBEEF));
    verify(userJpaRepository, times(1)).findById(UUID_DEADBEEF);
    verify(userMapper, times(0)).toResponse(any(User.class));
  }

  @Test
  void updateUser_shouldUpdateUserSuccessfully() {
    doNothing().when(authenticatedUserUtilService).checkIfUserIsAdminOrAccountOwner(UUID_DEADBEEF);
    when(userJpaRepository.findById(UUID_DEADBEEF)).thenReturn(Optional.of(TEST_USER));
    when(userMapper.toEntity(any(User.class), any(UpdateUserRequest.class)))
        .thenReturn(UPDATED_USER);
    when(userJpaRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userMapper.toResponse(any(User.class)))
        .thenAnswer(invocationOnMock -> UserResponse.of(invocationOnMock.getArgument(0)));

    var result = userService.updateUser(UUID_DEADBEEF, createUpdateRequest());

    assertEquals(UPDATED_EMAIL_EXAMPLE_COM, result.email());
    assertEquals(UPDATED_FIRST_NAME, result.firstName());
    assertEquals(UPDATED_LAST_NAME, result.lastName());
    assertEquals(UPDATED_PHONE_NUMBER, result.phoneNumber());
    verify(userJpaRepository, times(1)).findById(any(UUID.class));
    verify(authenticatedUserUtilService, times(1))
        .checkIfUserIsAdminOrAccountOwner(any(UUID.class));
    verify(userMapper, times(1)).toEntity(any(User.class), any(UpdateUserRequest.class));
    verify(userJpaRepository, times(1)).save(any(User.class));
    verify(userMapper, times(1)).toResponse(any(User.class));
  }

  @Test
  void updateUser_shouldThrowExceptionForInvalidUserId() {
    when(userJpaRepository.findById(UUID_DEADBEEF)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> userService.updateUser(UUID_DEADBEEF, UPDATE_USER_REQUEST));
    verify(userJpaRepository, times(1)).findById(any(UUID.class));
    verify(userMapper, times(0)).toEntity(any(User.class), any(UpdateUserRequest.class));
    verify(userJpaRepository, times(0)).save(any(User.class));
    verify(userMapper, times(0)).toResponse(any(User.class));
  }

  @Test
  void updateUser_shouldThrowExceptionIfUserIsNotAdminOrAccountOwner() {
    when(userJpaRepository.findById(UUID_DEADBEEF)).thenReturn(Optional.of(TEST_USER));
    doThrow(NoPermissionException.class)
        .when(authenticatedUserUtilService)
        .checkIfUserIsAdminOrAccountOwner(UUID_DEADBEEF);

    assertThrows(
        NoPermissionException.class,
        () -> userService.updateUser(UUID_DEADBEEF, UPDATE_USER_REQUEST));
    verify(authenticatedUserUtilService, times(1))
        .checkIfUserIsAdminOrAccountOwner(any(UUID.class));
    verify(userJpaRepository, times(1)).findById(any(UUID.class));
    verify(userMapper, times(0)).toEntity(any(User.class), any(UpdateUserRequest.class));
    verify(userJpaRepository, times(0)).save(any(User.class));
    verify(userMapper, times(0)).toResponse(any(User.class));
  }

  @Test
  void deleteUser_shouldDeleteUserSuccessfully() {
    when(userJpaRepository.findById(UUID_DEADBEEF)).thenReturn(Optional.of(TEST_USER));

    userService.deleteUser(UUID_DEADBEEF);

    verify(userJpaRepository, times(1)).findById(any(UUID.class));
    verify(userJpaRepository, times(1)).deleteById(any(UUID.class));
  }

  @Test
  void deleteUser_shouldThrowExceptionForInvalidUserId() {
    when(userJpaRepository.findById(UUID_DEADBEEF)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(UUID_DEADBEEF));
    verify(userJpaRepository, times(1)).findById(any(UUID.class));
    verify(userJpaRepository, times(0)).deleteById(any(UUID.class));
  }
}
