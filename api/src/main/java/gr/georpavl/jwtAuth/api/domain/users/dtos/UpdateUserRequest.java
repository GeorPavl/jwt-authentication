package gr.georpavl.jwtAuth.api.domain.users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
    @NotEmpty(message = "'Email' is required.")
        @Size(
            min = 4,
            max = 50,
            message = "'Email' must be between {min} and {max} characters long.")
        @Email(message = "You have to provide a valid email address.")
        String email,
    @NotEmpty(message = "'Last Name' is required.")
        @Size(min = 3, message = "'First Name must greater than {min} characters long.")
        String firstName,
    @NotEmpty(message = "'Last Name' is required.")
        @Size(min = 3, message = "'Last Name must greater than {min} characters long.")
        String lastName,
    @Size(min = 3, message = "'Last Name must greater than {min} characters long.")
        String phoneNumber) {

  public static UpdateUserRequest of(
      String email, String firstName, String lastName, String phoneNumber) {
    return UpdateUserRequest.builder()
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .phoneNumber(phoneNumber)
        .build();
  }
}
