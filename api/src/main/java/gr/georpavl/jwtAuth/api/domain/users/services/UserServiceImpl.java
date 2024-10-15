package gr.georpavl.jwtAuth.api.domain.users.services;

import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User createUser(User user) {
    return userJpaRepository.save(user);
  }
}
