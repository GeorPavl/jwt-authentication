package gr.georpavl.jwtAuth.api.domain.users.services;

import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {
  List<UserResponse> getAllUsers();

  UserResponse getUserById(UUID userId);

  UserResponse updateUser(UUID userId, UpdateUserRequest request);

  void deleteUser(UUID userId);
}
