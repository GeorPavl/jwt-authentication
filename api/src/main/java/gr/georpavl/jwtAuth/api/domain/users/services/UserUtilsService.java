package gr.georpavl.jwtAuth.api.domain.users.services;

import gr.georpavl.jwtAuth.api.domain.users.Role;
import gr.georpavl.jwtAuth.api.security.exceptions.handlers.SecurityExceptionFactory;
import gr.georpavl.jwtAuth.api.security.userDetails.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUtilsService {

  public UserDetailsImpl getLoggedInUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
      return (UserDetailsImpl) authentication.getPrincipal();
    }

    throw new IllegalStateException("User is not logged in");
  }

  public void checkIfUserIsAdminOrAccountOwner(UUID userId) {
    if (!isLoggedUserAccountOwner(userId) && !isLoggedUserAdmin()) {
      throw SecurityExceptionFactory.of(new AccessDeniedException("The user lacks permissions."));
    }
  }

  private boolean isLoggedUserAdmin() {
    return getLoggedInUser().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(authority -> authority.equals(Role.ADMIN.name()));
  }

  public boolean isLoggedUserAccountOwner(UUID userId) {
    return getLoggedInUser().getId().equals(userId);
  }
}
