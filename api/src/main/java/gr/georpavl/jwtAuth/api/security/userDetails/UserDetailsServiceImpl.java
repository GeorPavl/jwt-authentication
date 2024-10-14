package gr.georpavl.jwtAuth.api.security.userDetails;

import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserJpaRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = userRepository.findByEmail(username);
    return user.map(UserDetailsImpl::new)
        .orElseThrow(() -> new UsernameNotFoundException("Invalid Username"));
  }
}
