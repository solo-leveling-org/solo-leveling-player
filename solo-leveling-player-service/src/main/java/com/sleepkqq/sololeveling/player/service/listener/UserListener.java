package com.sleepkqq.sololeveling.player.service.listener;

import com.sleepkqq.sololeveling.player.service.model.Player;
import com.sleepkqq.sololeveling.player.service.model.User;
import jakarta.persistence.PrePersist;

public class UserListener {

  @PrePersist
  public void onPrePersist(User user) {
    var userTasks = Player.init(user);
    user.setPlayer(userTasks);
  }
}
