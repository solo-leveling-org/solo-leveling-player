package com.soloist.player.model.entity.task.enums;

import org.babyfish.jimmer.sql.EnumItem;

public enum TaskType {

  @EnumItem(ordinal = 0)
  STEPS,

  @EnumItem(ordinal = 1)
  PUSH_UPS,

  @EnumItem(ordinal = 2)
  SQUATS,

  @EnumItem(ordinal = 3)
  CUSTOM
}
