package com.soloist.player.model.entity.player.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum Rarity implements LocalizableEnum {
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
