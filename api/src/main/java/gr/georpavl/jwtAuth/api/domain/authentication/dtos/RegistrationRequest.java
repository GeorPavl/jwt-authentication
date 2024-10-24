package gr.georpavl.jwtAuth.api.domain.authentication.dtos;

import gr.georpavl.jwtAuth.api.domain.users.Role;
import gr.georpavl.jwtAuth.api.utils.validators.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegistrationRequest(
    @NotEmpty(message = "'Email' is required.")
        @Size(
            min = 4,
            max = 50,
            message = "'Email' must be between {min} and {max} characters long.")
        @Email(message = "You have to provide a valid email address.")
        String email,
    @NotEmpty(message = "'First Name' is required.")
        @Size(min = 3, message = "'First Name must greater than {min} characters long.")
        String firstName,
    @NotEmpty(message = "'Last Name' is required.")
        @Size(min = 3, message = "'Last Name must greater than {min} characters long.")
        String lastName,
    @NotEmpty(message = "'Last Name' is required.")
        @Size(min = 3, message = "'Phone Number' must greater than {min} characters long.")
        String phoneNumber,
    @ValidPassword String password,
    @ValidPassword String confirmationPassword,
    @NotNull(message = "'Role' is required.") Role role) {

  public static RegistrationRequest of(
      String email,
      String firstName,
      String lastName,
      String phoneNumber,
      String password,
      String confirmationPassword,
      Role role) {
    return RegistrationRequest.builder()
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .phoneNumber(phoneNumber)
        .password(password)
        .confirmationPassword(confirmationPassword)
        .role(role)
        .build();
  }
}
