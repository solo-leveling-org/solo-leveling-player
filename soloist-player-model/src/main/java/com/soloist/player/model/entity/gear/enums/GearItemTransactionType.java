package com.soloist.player.model.entity.gear.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum GearItemTransactionType implements LocalizableEnum {

  @EnumItem(ordinal = 0)
  DROPPED,

  @EnumItem(ordinal = 1)
  QUICK_SOLD,

  @EnumItem(ordinal = 2)
  MARKETPLACE_LISTED,

  @EnumItem(ordinal = 3)
  MARKETPLACE_SOLD,

  @EnumItem(ordinal = 4)
  TRADED,

  @EnumItem(ordinal = 5)
  ENCHANTED
}
