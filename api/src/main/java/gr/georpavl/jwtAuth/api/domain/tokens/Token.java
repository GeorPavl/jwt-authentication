package gr.georpavl.jwtAuth.api.domain.tokens;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.utils.generators.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Token {

  @Id
  @EqualsAndHashCode.Include
  @Column(name = "id", columnDefinition = "uuid", updatable = false)
  private UUID id = UUIDGenerator.generateUUID();

  @Column(name = "value", unique = true)
  public String value;

  @Column(name = "is_revoked")
  public boolean revoked;

  @Column(name = "is_expired")
  public boolean expired;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;

  public static Token of(UUID id, String value, boolean isRevoked, boolean isExpired, User user) {
    return Token.builder()
        .id(id)
        .value(value)
        .revoked(isRevoked)
        .expired(isExpired)
        .user(user)
        .build();
  }

  public static Token of(String value, boolean isRevoked, boolean isExpired, User user) {
    return Token.builder()
        .id(UUIDGenerator.generateUUID())
        .value(value)
        .revoked(isRevoked)
        .expired(isExpired)
        .user(user)
        .build();
  }
}
