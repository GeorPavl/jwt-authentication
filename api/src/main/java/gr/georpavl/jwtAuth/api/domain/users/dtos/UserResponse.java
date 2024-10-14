package gr.georpavl.jwtAuth.api.domain.users.dtos;

import gr.georpavl.jwtAuth.api.domain.users.User;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(
    UUID id, String email, String firstName, String lastName, String phoneNumber) {

  public static UserResponse of(
      UUID id, String email, String firstName, String lastName, String phoneNumber) {
    return UserResponse.builder()
        .id(id)
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .phoneNumber(phoneNumber)
        .build();
  }

  public static UserResponse of(User user) {
    return of(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhoneNumber());
  }
}
