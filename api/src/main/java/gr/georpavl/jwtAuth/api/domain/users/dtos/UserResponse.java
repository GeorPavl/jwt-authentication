package gr.georpavl.jwtAuth.api.domain.users.dtos;

import gr.georpavl.jwtAuth.api.domain.users.Role;
import gr.georpavl.jwtAuth.api.domain.users.User;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(
    UUID id, String email, String firstName, String lastName, String phoneNumber, Role role) {

  public static UserResponse of(
      UUID id, String email, String firstName, String lastName, String phoneNumber, Role role) {
    return UserResponse.builder()
        .id(id)
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .phoneNumber(phoneNumber)
        .role(role)
        .build();
  }

  public static UserResponse of(User user) {
    return of(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhoneNumber(),
        user.getRole());
  }
}
