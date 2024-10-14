package gr.georpavl.jwtAuth.api.domain.authentication.dtos;

import lombok.Builder;

@Builder
public record RefreshTokenRequest(String email, String refreshToken) {

  public static RefreshTokenRequest of(String email, String refreshToken) {
    return RefreshTokenRequest.builder().email(email).refreshToken(refreshToken).build();
  }
}