package gr.georpavl.jwtAuth.api.domain.tokens.repositories;

import gr.georpavl.jwtAuth.api.domain.tokens.Token;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository extends JpaRepository<Token, UUID> {
  Optional<Token> findByUserToken(String token);
}
