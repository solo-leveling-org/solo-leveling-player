package com.sleepkqq.sololeveling.player.service.service.user;

import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.user.User;
import com.sleepkqq.sololeveling.player.service.service.player.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

  private static final int BASE_PLAYER_MAX_TASKS = 5;

  private final LevelService levelService;

  public void register(User user) {
    var player = Player.builder()
        .maxTasks(BASE_PLAYER_MAX_TASKS)
        .user(user)
        .build();

    levelService.initializePlayerLevel(player);
    user.setPlayer(player);
  }
}
