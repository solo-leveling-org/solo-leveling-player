package com.sleepkqq.sololeveling.user.service.service;

import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User getById(long id) {
    return findById(id).orElseThrow(() -> new IllegalArgumentException("User not found id=" + id));
  }

  public Optional<User> findById(long id) {
    return userRepository.findById(id);
  }

  public User save(User user) {
    return userRepository.save(user);
  }
}
