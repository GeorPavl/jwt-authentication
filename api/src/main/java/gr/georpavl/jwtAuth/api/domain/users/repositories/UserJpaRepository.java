package gr.georpavl.jwtAuth.api.domain.users.repositories;

import gr.georpavl.jwtAuth.api.domain.users.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String userEmail);

  Optional<User> findByCode(Integer code);
  //  Optional<User> findByTokenAndCode(String token, Integer code);
}
