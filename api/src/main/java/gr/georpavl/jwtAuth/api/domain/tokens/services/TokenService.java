package gr.georpavl.jwtAuth.api.domain.tokens.services;

import gr.georpavl.jwtAuth.api.domain.tokens.Token;
import gr.georpavl.jwtAuth.api.domain.users.User;
import java.util.UUID;

public interface TokenService {
  Token createToken(User user, String token);

  void revokeUsersTokens(UUID userId);
}
