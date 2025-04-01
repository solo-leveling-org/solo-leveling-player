package com.sleepkqq.sololeveling.player.service.model.user.enums;

import org.babyfish.jimmer.sql.EnumItem;

public enum UserRole {
  @EnumItem(ordinal = 0)
  USER,
  @EnumItem(ordinal = 1)
  ADMIN
}
