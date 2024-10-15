package gr.georpavl.jwtAuth.api.domain.users.services;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;

public interface UserService {
  User createUser(User user);
}
