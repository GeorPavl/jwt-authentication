package gr.georpavl.jwtAuth.api.domain.authentication.services;

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

  /**
   * Scheduled task that runs every midnight to clean up expired user accounts. Deletes users who
   * have not verified their accounts and whose token expiration time has passed. Τhe task runs
   * every day at 00:00
   */
  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  public void removeExpiredUsersAndTokens() {
    LocalDateTime now = LocalDateTime.now();
    userJpaRepository.deleteExpiredUsers(now);
    log.info("Scheduled task was executed successfully, users were cleaned up");
  }
}
