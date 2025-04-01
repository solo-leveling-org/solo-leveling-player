package com.sleepkqq.sololeveling.player.service.service.user;

import com.sleepkqq.sololeveling.player.service.model.Immutables;
import com.sleepkqq.sololeveling.player.service.model.user.User;
import com.sleepkqq.sololeveling.player.service.service.player.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

  private static final int BASE_PLAYER_MAX_TASKS = 5;

  private final LevelService levelService;

  public User register(User user) {
    return Immutables.createUser(user, u -> {
      u.applyPlayer(player -> {
        player.setId(u.id());
        player.setMaxTasks(BASE_PLAYER_MAX_TASKS);
        player.applyLevel(levelService::initializePlayerLevel);
      });
    });
  }
}
