package com.sleepkqq.sololeveling.player.model.entity.task.enums;

import org.babyfish.jimmer.sql.EnumItem;

public enum TaskRarity {
  @EnumItem(ordinal = 0)
  COMMON,

  @EnumItem(ordinal = 1)
  UNCOMMON,

  @EnumItem(ordinal = 2)
  RARE,

  @EnumItem(ordinal = 3)
  EPIC,

  @EnumItem(ordinal = 4)
  LEGENDARY
}
