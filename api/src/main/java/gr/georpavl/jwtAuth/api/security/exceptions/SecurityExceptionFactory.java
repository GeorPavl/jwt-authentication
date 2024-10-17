package gr.georpavl.jwtAuth.api.security.exceptions;

import gr.georpavl.jwtAuth.api.security.exceptions.implementations.NoPermissionException;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UnauthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class SecurityExceptionFactory {

  public static RuntimeException handleSecurityException(Exception e) {
    if (e instanceof AuthenticationException) {
      return of((AuthenticationException) e);
    } else if (e instanceof AccessDeniedException) {
      return of((AccessDeniedException) e);
    } else {
      return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
  }

  public static RuntimeException of(AuthenticationException e) {
    if (e instanceof BadCredentialsException) {
      return new UnauthorizedAccessException("Invalid credentials provided.");
    } else if (e instanceof UsernameNotFoundException) {
      return new UnauthorizedAccessException("User not found.");
    } else if (e instanceof DisabledException) {
      return new UnauthorizedAccessException("User account is disabled.");
    } else if (e instanceof LockedException) {
      return new UnauthorizedAccessException("User account is locked.");
    } else if (e instanceof CredentialsExpiredException) {
      return new UnauthorizedAccessException("Your credentials have expired.");
    } else if (e instanceof AccountExpiredException) {
      return new UnauthorizedAccessException("Your account has expired.");
    } else if (e instanceof InsufficientAuthenticationException) {
      return new UnauthorizedAccessException("Insufficient authentication provided.");
    } else if (e instanceof ProviderNotFoundException) {
      return new UnauthorizedAccessException("Authentication provider not found.");
    } else {
      return new UnauthorizedAccessException("Authentication failed due to an unknown error.");
    }
  }

  public static RuntimeException of(AccessDeniedException e) {
    if (e instanceof AuthorizationServiceException) {
      return new NoPermissionException("Authorization service encountered an error.");
    } else if (e.getMessage().contains("denyAll")) {
      return new NoPermissionException("Access to this resource is completely restricted.");
    } else if (e.getMessage().contains("ROLE_ADMIN")) {
      return new NoPermissionException("You need admin privileges to access this resource.");
    } else if (e.getMessage().contains("ROLE_USER")) {
      return new NoPermissionException("Only user roles can access this resource.");
    } else if (e.getMessage().contains("permission denied")) {
      return new NoPermissionException("Permission denied to access this resource.");
    } else {
      // Προεπιλεγμένο μήνυμα για όλες τις άλλες περιπτώσεις `AccessDeniedException`
      return new NoPermissionException("Access denied due to insufficient permissions.");
    }
  }

}
