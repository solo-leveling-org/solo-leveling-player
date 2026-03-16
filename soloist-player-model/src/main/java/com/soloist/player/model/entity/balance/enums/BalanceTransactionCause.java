package com.soloist.player.model.entity.balance.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import org.babyfish.jimmer.sql.EnumItem;

public enum BalanceTransactionCause implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  TASK_COMPLETION,

  @EnumItem(ordinal = 1)
  LEVEL_UP,

  @EnumItem(ordinal = 2)
  DAILY_CHECK_IN,

  @EnumItem(ordinal = 3)
  ITEM_PURCHASE
}
