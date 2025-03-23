package com.sleepkqq.sololeveling.player.service.service.user;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.user.User;
import com.sleepkqq.sololeveling.player.service.repository.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserRegistrationService userRegistrationService;

  public User get(long id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(User.class, id));
  }

  public Optional<User> find(long id) {
    return userRepository.findById(id);
  }

  @Transactional
  public User createOrUpdate(User user) {
    userRepository.findById(user.getId())
        .ifPresentOrElse(existingUser -> {
              user.setVersion(existingUser.getVersion());
              user.setPlayer(existingUser.getPlayer());
            },
            () -> userRegistrationService.register(user)
        );
    return userRepository.save(user);
  }
}
