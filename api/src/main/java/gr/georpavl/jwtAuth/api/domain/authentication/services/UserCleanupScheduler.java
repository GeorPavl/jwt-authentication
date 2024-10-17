package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCleanupScheduler {

  private final UserJpaRepository userJpaRepository;
  private final TokenJpaRepository tokenJpaRepository;

  /**
   * Scheduled task that runs every midnight to clean up expired user accounts and tokens.
   *
   * <p>This method performs two main functions: - Deletes users who have not verified their
   * accounts and whose token expiration time has passed. - Removes revoked and expired tokens from
   * the token repository to free up resources.
   *
   * <p>Τhe task runs every day at 00:00
   */
  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  public void removeExpiredUsersAndTokens() {
    LocalDateTime now = LocalDateTime.now();
    tokenJpaRepository.deleteRevokedAndExpiredTokens();
    userJpaRepository.deleteExpiredUsers(now);
    log.info("Scheduled task was executed successfully, users and tokens were cleaned up");
  }
}
