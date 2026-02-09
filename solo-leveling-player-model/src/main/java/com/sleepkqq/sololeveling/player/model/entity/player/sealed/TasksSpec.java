package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

public interface TasksSpec extends DailyTaskSpec {

  @Override
  default DailyTaskType type() {
    return DailyTaskType.TASKS;
  }
}
