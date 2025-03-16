package com.sleepkqq.sololeveling.user.service.listener;

import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.model.UserTasks;
import jakarta.persistence.PrePersist;

public class UserListener {

  @PrePersist
  public void onPrePersist(User user) {
    var userTasks = UserTasks.init(user);
    user.setUserTasks(userTasks);
  }
}
