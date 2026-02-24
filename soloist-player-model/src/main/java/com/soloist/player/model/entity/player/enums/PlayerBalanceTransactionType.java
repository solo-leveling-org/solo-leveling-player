package com.soloist.player.model.entity.player.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum PlayerBalanceTransactionType implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  IN,

  @EnumItem(ordinal = 1)
  OUT
}
