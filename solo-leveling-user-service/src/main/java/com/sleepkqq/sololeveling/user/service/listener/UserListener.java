package com.sleepkqq.sololeveling.user.service.listener;

import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.model.Player;
import jakarta.persistence.PrePersist;

public class UserListener {

  @PrePersist
  public void onPrePersist(User user) {
    var userTasks = Player.init(user);
    user.setPlayer(userTasks);
  }
}
