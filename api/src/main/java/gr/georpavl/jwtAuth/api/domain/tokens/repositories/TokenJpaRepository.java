package gr.georpavl.jwtAuth.api.domain.tokens.repositories;

import gr.georpavl.jwtAuth.api.domain.tokens.Token;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenJpaRepository extends JpaRepository<Token, UUID> {
  @Query(
      value =
          """
          SELECT t FROM Token t
          INNER JOIN User u on t.user.id = u.id
          where u.id = :userId
          and (t.expired = false or t.revoked = false)
        """)
  List<Token> findAllValidTokenByUser(UUID userId);

  Optional<Token> findByUserToken(String token);

  @Transactional
  @Modifying
  @Query("DELETE FROM Token t WHERE t.revoked = true AND t.expired = true")
  void deleteRevokedAndExpiredTokens();
}
