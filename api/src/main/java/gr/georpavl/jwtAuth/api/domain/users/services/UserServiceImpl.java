package gr.georpavl.jwtAuth.api.domain.users.services;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import javax.naming.NoPermissionException;
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
  private final UserUtilsService userUtilsService;

  @Override
  public List<User> getAllUsers() {
    return userJpaRepository.findAll();
  }

  @Override
  public User getUserById(UUID userId) {
    return userJpaRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(User.class.getSimpleName(), "ID", userId.toString()));
  }

  @Override
  public User createUser(User user) {
    return userJpaRepository.save(user);
  }

  @Transactional
  @Override
  public User updateUser(UUID userId, UpdateUserRequest request) throws NoPermissionException {
    try {
      userUtilsService.checkIfUserIsAdminOrAccountOwner(userId);
      var user =
          userJpaRepository
              .findById(userId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          User.class.getSimpleName(), "ID", userId.toString()));
      var userToUpdate = userMapper.toEntity(user, request);
      return userJpaRepository.save(userToUpdate);
    } catch (Exception e) {
      log.error("Error during updating user {}", request.email(), e);
      throw e;
    }
  }

  @Override
  public void deleteUser(UUID userId) {
    userJpaRepository.deleteById(userId);
  }
}
