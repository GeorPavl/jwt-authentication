package gr.georpavl.jwtAuth.api.domain.users.services;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import java.util.List;
import java.util.UUID;
import javax.naming.NoPermissionException;

public interface UserService {
  List<User> getAllUsers();

  User getUserById(UUID userId);

  User createUser(User user);

  User updateUser(UUID userId, UpdateUserRequest request) throws NoPermissionException;

  void deleteUser(UUID userId);
}
