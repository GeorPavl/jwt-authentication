package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceAlreadyPresentException;
import gr.georpavl.jwtAuth.api.utils.generators.TokenGenerator;
import gr.georpavl.jwtAuth.api.utils.mailService.MailService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVerificationService {

  private final UserJpaRepository userJpaRepository;
  private final MailService mailService;

  public void verify(String token, Integer code) {
    var user = getUserIfSessionNotExpired(code);
    isVerificationTokenValid(token, user);
    enableAndVerifyUserAccount(user);
  }

  public void resendVerificationEmail(String userEmail) {
    var user = findUserOrElseThrow(userEmail);
    if (user.isEnabled()) {
      throw new ResourceAlreadyPresentException("User is already verified");
    }
    user.setToken(TokenGenerator.generateToken());
    user.setTokenExpiration(LocalDateTime.now().plusHours(24));
    userJpaRepository.save(user);
    sendVerificationEmail(user);
  }

  private void sendVerificationEmail(User user) {
    mailService.sendVerificationEmail(user.getEmail(), user.getToken(), user.getCode());
  }

  private User getUserIfSessionNotExpired(Integer code) {
    return userJpaRepository
        .findByCode(code)
        .orElseThrow(() -> new SessionAuthenticationException("SESSION_EXPIRED"));
  }

  private void isVerificationTokenValid(String token, User user) {
    if (!user.getToken().equals(token)) {
      throw new SessionAuthenticationException("Invalid token");
    }
    if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
      throw new SessionAuthenticationException("Token was expired");
    }
  }

  private void enableAndVerifyUserAccount(User user) {
    try {
      user.setEnabled(true);
      user.setVerified(true);
      user.setVerifiedAt(LocalDateTime.now());
      user.setCode(null);
      user.setToken(null);
      user.setTokenExpiration(null);
      userJpaRepository.save(user);
    } catch (Exception e) {
      throw new SessionAuthenticationException("Error saving user verification details.");
    }
  }

  private User findUserOrElseThrow(String email) {
    return userJpaRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User %s not found", email)));
  }
}
