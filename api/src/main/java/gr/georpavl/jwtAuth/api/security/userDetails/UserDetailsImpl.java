package gr.georpavl.jwtAuth.api.security.userDetails;

import gr.georpavl.jwtAuth.api.domain.users.User;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
public class UserDetailsImpl implements UserDetails {

  private final String username;
  private final String password;
  private final List<GrantedAuthority> authorities;
  private final boolean enabled;
  private final boolean verified;

  public UserDetailsImpl(User user) {
    this.username = user.getEmail();
    this.password = user.getPassword();
    this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
    this.enabled = user.isEnabled();
    this.verified = user.isVerified();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.enabled;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.enabled && this.verified;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.enabled;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}
