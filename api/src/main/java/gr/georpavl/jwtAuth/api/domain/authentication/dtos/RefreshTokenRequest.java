package gr.georpavl.jwtAuth.api.domain.authentication.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
    @NotEmpty(message = "Please provide your email address.")
        @Email(message = "Please provide a valid email address.")
        @Size(
            min = 5,
            max = 100,
            message = "Email should be between {min} and {max} characters long.")
        String email,
    @NotEmpty(message = "Please provide a valid token.") String refreshToken) {

  public static RefreshTokenRequest of(String email, String refreshToken) {
    return RefreshTokenRequest.builder().email(email).refreshToken(refreshToken).build();
  }
}
