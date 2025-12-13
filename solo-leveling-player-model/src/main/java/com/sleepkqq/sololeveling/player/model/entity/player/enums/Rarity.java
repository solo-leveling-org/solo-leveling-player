package com.sleepkqq.sololeveling.player.model.entity.player.enums;

import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
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
