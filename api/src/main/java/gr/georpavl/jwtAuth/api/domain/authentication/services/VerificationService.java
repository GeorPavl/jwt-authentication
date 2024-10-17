package gr.georpavl.jwtAuth.api.domain.authentication.services;

public interface VerificationService {
  void verify(String token, Integer code);

  void resendVerificationEmail(String userEmail);
}
