package com.sleepkqq.sololeveling.user.service.service;

import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.model.UserTasks;
import com.sleepkqq.sololeveling.user.service.repository.UserRepository;
import com.sleepkqq.sololeveling.user.service.repository.UserTasksRepository;
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
  private final UserTasksRepository userTasksRepository;

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
          user.setUserTasks(existingUser.getUserTasks());
        });
    return userRepository.save(user);
  }

  public UserTasks getUserTasks(long id) {
    return userTasksRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found userId=" + id));
  }

  public UserTasks saveUserTasks(UserTasks userTasks) {
    return userTasksRepository.save(userTasks);
  }
}
