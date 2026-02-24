package com.soloist.player.model.entity.user.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
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
  MANAGER
}
