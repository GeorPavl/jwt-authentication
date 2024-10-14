package gr.georpavl.jwtAuth.api.domain.users;

import gr.georpavl.jwtAuth.api.utils.generators.RandomCodeGenerator;
import gr.georpavl.jwtAuth.api.utils.generators.TokenGenerator;
import gr.georpavl.jwtAuth.api.utils.generators.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @Id
  @EqualsAndHashCode.Include
  @Column(name = "id", columnDefinition = "uuid", updatable = false)
  private UUID id = UUIDGenerator.generateUUID();

  @Column(name = "email", unique = true)
  @NotEmpty(message = "'Email' is required.")
  @Size(min = 4, max = 50, message = "'Email' must be between {min} and {max} characters long.")
  @Email(message = "You have to provide a valid email address.")
  private String email;

  @Column(name = "first_name")
  @NotEmpty(message = "'Last Name' is required.")
  @Size(min = 3, message = "'First Name must greater than {min} characters long.")
  private String firstName;

  @Column(name = "last_name")
  @NotEmpty(message = "'Last Name' is required.")
  @Size(min = 3, message = "'Last Name must greater than {min} characters long.")
  private String lastName;

  @Column(name = "phone_number")
  private String phoneNumber;

  @NotEmpty(message = "'Password' is required.")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Role role;

  @Column(name = "enabled", columnDefinition = "TINYINT", length = 1)
  @NotNull(message = "'Enabled' is required.")
  private boolean enabled;

  @Column(name = "verified", columnDefinition = "TINYINT", length = 1)
  @NotNull(message = "'Verified' is required.")
  private boolean verified;

  @Column(name = "verified_at", columnDefinition = "timestamp")
  private LocalDateTime verifiedAt;

  @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(
      name = "updated_at",
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(name = "token")
  private String token = TokenGenerator.generateToken();

  @Column(name = "code")
  private Integer code = RandomCodeGenerator.generateRandomCode();

  public static User of(
      String email, String firstName, String lastName, String phoneNumber, String password) {
    return User.builder()
        .id(UUIDGenerator.generateUUID())
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .phoneNumber(phoneNumber)
        .password(password)
        .role(Role.USER)
        .enabled(false)
        .verified(false)
        .code(RandomCodeGenerator.generateRandomCode())
        .token(TokenGenerator.generateToken())
        .build();
  }

  public static User of(
      UUID id,
      String email,
      String firstName,
      String lastName,
      String phoneNumber,
      String password,
      Role role,
      boolean enabled,
      boolean verified,
      Integer code,
      String token) {
    return User.builder()
        .id(id)
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .phoneNumber(phoneNumber)
        .password(password)
        .role(role)
        .enabled(enabled)
        .verified(verified)
        .code(code)
        .token(token)
        .build();
  }

  public static User of(
      UUID id,
      String email,
      String firstName,
      String lastName,
      String phoneNumber,
      String password,
      Role role,
      LocalDateTime createdAt,
      boolean enabled,
      boolean verified,
      LocalDateTime verifiedAt,
      LocalDateTime updatedAt,
      String token,
      int code) {
    return User.builder()
        .id(id)
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .phoneNumber(phoneNumber)
        .password(password)
        .role(role)
        .createdAt(createdAt)
        .enabled(enabled)
        .verified(verified)
        .verifiedAt(verifiedAt)
        .updatedAt(updatedAt)
        .code(code)
        .token(token)
        .build();
  }
}
