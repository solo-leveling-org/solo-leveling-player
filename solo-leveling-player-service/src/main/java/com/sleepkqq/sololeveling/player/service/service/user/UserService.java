package com.sleepkqq.sololeveling.player.service.service.user;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.Immutables;
import com.sleepkqq.sololeveling.player.service.model.user.User;
import com.sleepkqq.sololeveling.player.service.repository.user.UserRepository;
import java.time.LocalDateTime;
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

  @Transactional
  public User get(long id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(User.class, id));
  }

  @Transactional
  public Optional<User> find(long id) {
    return userRepository.findById(id);
  }

  @Transactional
  public Optional<Integer> findVersion(long id) {
    return userRepository.findVersionById(id);
  }

  @Transactional
  public User create(User user) {
    return userRepository.save(user);
  }

  @Transactional
  public User update(User user, LocalDateTime now) {
    return userRepository.update(Immutables.createUser(user, u ->
        u.setUpdatedAt(now)
    ));
  }

  @Transactional
  public User update(User user) {
    return update(user, LocalDateTime.now());
  }

  @Transactional
  public User upsert(User user) {
    var now = LocalDateTime.now();
    return findVersion(user.id())
        .map(v -> update(Immutables.createUser(user, us -> {
              us.setVersion(v);
              us.setLastLoginAt(now);
            }),
            now
        ))
        .orElseGet(() -> create(userRegistrationService.register(user)));
  }
}
