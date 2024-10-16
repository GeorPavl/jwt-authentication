package gr.georpavl.jwtAuth.api.domain.users.repositories;

import gr.georpavl.jwtAuth.api.domain.users.User;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String userEmail);

  Optional<User> findByCode(Integer code);

  @Modifying
  @Query("DELETE FROM User u WHERE u.enabled = false AND u.tokenExpiration < :now")
  void deleteExpiredUsers(@Param("now") LocalDateTime now);
}
