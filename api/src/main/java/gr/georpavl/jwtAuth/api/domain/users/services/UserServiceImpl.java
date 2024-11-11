package gr.georpavl.jwtAuth.api.domain.users.services;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.services.AuthenticatedUserUtilService;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserJpaRepository userJpaRepository;
  private final UserMapper userMapper;
  private final AuthenticatedUserUtilService authenticatedUserUtilService;

  @Override
  public List<UserResponse> getAllUsers() {
    return userJpaRepository.findAll().stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public UserResponse getUserById(UUID userId) {
    var user =
        userJpaRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        User.class.getSimpleName(), "ID", userId.toString()));
    return userMapper.toResponse(user);
  }

  @Transactional
  @Override
  public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
    var user =
        userJpaRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        User.class.getSimpleName(), "ID", userId.toString()));
    authenticatedUserUtilService.checkIfUserIsAdminOrAccountOwner(userId);
    var userToUpdate = userMapper.toEntity(user, request);
    var savedUser = userJpaRepository.save(userToUpdate);
    return userMapper.toResponse(savedUser);
  }

  @Override
  public void deleteUser(UUID userId) {
    getUserById(userId);
    userJpaRepository.deleteById(userId);
  }
}
