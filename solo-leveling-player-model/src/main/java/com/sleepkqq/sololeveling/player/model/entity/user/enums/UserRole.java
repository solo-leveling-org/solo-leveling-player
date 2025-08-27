package com.sleepkqq.sololeveling.player.model.entity.user.enums;

import org.babyfish.jimmer.sql.EnumItem;

public enum UserRole {
  @EnumItem(ordinal = 0)
  USER,

  @EnumItem(ordinal = 1)
  ADMIN
}
