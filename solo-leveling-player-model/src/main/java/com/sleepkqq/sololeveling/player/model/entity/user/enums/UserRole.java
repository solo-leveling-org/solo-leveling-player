package com.sleepkqq.sololeveling.player.model.entity.user.enums;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum UserRole implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  USER,

  @EnumItem(ordinal = 1)
  ADMIN,

  @EnumItem(ordinal = 2)
  DEVELOPER,

  @EnumItem(ordinal = 3)
  MANAGER;

  private final String path = EnumPathGenerator.generatePath(this);
}
