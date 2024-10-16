package gr.georpavl.jwtAuth.api.domain.tokens.services;

import gr.georpavl.jwtAuth.api.domain.tokens.Token;
import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.utils.generators.UUIDGenerator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

  private final TokenJpaRepository tokenJpaRepository;

  @Override
  public Token createToken(User user, String value) {
    var token = Token.of(UUIDGenerator.generateUUID(), value, false, false, user);
    return tokenJpaRepository.save(token);
  }

  @Override
  public void revokeUsersTokens(UUID userId) {
    var validUserTokens = tokenJpaRepository.findAllValidTokenByUser(userId);

    if (validUserTokens.isEmpty()) {
      return;
    }

    validUserTokens.forEach(
        token -> {
          token.setExpired(true);
          token.setRevoked(true);
        });
    tokenJpaRepository.saveAll(validUserTokens);
  }
}
