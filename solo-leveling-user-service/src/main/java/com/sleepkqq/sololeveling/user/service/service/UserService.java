package com.sleepkqq.sololeveling.user.service.service;

import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.repository.UserRepository;
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

  public User get(long id) {
    return find(id).orElseThrow(() -> new IllegalArgumentException("User not found userId=" + id));
  }

  public Optional<User> find(long id) {
    return userRepository.findById(id);
  }

  @Transactional
  public User createOrUpdate(User user) {
    userRepository.findById(user.getId())
        .ifPresent(existingUser -> {
          user.setVersion(existingUser.getVersion());
          user.setPlayer(existingUser.getPlayer());
        });
    return userRepository.save(user);
  }
}
