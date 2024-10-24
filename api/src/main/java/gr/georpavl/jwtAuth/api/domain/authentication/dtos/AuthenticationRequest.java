package gr.georpavl.jwtAuth.api.domain.authentication.dtos;

import gr.georpavl.jwtAuth.api.utils.validators.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthenticationRequest(
    @NotEmpty(message = "Please provide your email address.")
        @Email(message = "Please provide a valid email address.")
        @Size(
            min = 5,
            max = 100,
            message = "Email should be between {min} and {max} characters long.")
        String email,
    @NotEmpty(message = "Please provide your password.") @ValidPassword String password) {

  public static AuthenticationRequest of(String email, String password) {
    return AuthenticationRequest.builder().email(email).password(password).build();
  }
}
