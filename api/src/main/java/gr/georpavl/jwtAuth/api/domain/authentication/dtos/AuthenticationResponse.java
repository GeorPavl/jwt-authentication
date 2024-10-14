package gr.georpavl.jwtAuth.api.domain.authentication.dtos;

import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
    UserResponse userResponse, String accessToken, String refreshToken) {

  public static AuthenticationResponse of(
      UserResponse userResponse, String accessToken, String refreshToken) {
    return AuthenticationResponse.builder()
        .userResponse(userResponse)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }
}
