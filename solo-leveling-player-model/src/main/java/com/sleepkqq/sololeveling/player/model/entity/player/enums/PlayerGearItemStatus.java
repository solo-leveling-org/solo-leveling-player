package com.sleepkqq.sololeveling.player.model.entity.player.enums;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum PlayerGearItemStatus {
  @EnumItem(ordinal = 0)
  IN_INVENTORY,

  @EnumItem(ordinal = 1)
  EQUIPPED,

  @EnumItem(ordinal = 2)
  QUICK_SOLD,

  @EnumItem(ordinal = 3)
  MARKETPLACE_SOLD;

  private final String path = EnumPathGenerator.generatePath(this);
}
