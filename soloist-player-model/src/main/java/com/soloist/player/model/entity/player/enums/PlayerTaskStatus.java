package com.soloist.player.model.entity.player.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum PlayerTaskStatus implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  PREPARING,

  @EnumItem(ordinal = 1)
  IN_PROGRESS,

  @EnumItem(ordinal = 2)
  COMPLETED,

  @EnumItem(ordinal = 3)
  SKIPPED
}
