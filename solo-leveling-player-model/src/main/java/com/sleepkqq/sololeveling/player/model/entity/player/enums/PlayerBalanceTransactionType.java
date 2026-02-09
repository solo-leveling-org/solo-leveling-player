package com.sleepkqq.sololeveling.player.model.entity.player.enums;

import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum PlayerBalanceTransactionType implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  IN,

  @EnumItem(ordinal = 1)
  OUT
}
