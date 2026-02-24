package com.soloist.player.model.entity.player.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum PlayerGearItemStatus implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  IN_INVENTORY,

  @EnumItem(ordinal = 1)
  EQUIPPED,

  @EnumItem(ordinal = 2)
  QUICK_SOLD,

  @EnumItem(ordinal = 3)
  MARKETPLACE_SOLD
}
