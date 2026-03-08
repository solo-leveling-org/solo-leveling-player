package com.soloist.player.model.entity.balance.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum BalanceTransactionType implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  IN,

  @EnumItem(ordinal = 1)
  OUT
}
