package gr.georpavl.jwtAuth.api.domain.users.controllers;

import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.domain.users.mappers.UserMapper;
import gr.georpavl.jwtAuth.api.domain.users.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.naming.NoPermissionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAll() {
    var result =
        userService.getAllUsers().stream().map(userMapper::toResponse).collect(Collectors.toList());
    log.info("All users fetched successfully");
    return ResponseEntity.ok().body(result);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getById(@PathVariable("userId") UUID userId) {
    var result = userService.getUserById(userId);
    log.info("User fetched successfully");
    return ResponseEntity.ok().body(UserResponse.of(result));
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable("userId") UUID userId, @RequestBody @Valid UpdateUserRequest request)
      throws NoPermissionException {
    var result = userService.updateUser(userId, request);
    log.info("User updated successfully");
    return ResponseEntity.accepted().body(UserResponse.of(result));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteById(@PathVariable("userId") UUID userId) {
    userService.deleteUser(userId);
    log.info("User deleted successfully");
    return ResponseEntity.noContent().build();
  }
}