package com.sleepkqq.sololeveling.player.model.entity.player.enums;

import org.babyfish.jimmer.sql.EnumItem;

public enum PlayerBalanceTransactionCause {
  @EnumItem(ordinal = 0)
  TASK_COMPLETION,

  @EnumItem(ordinal = 1)
  LEVEL_UP,

  @EnumItem(ordinal = 2)
  DAILY_CHECK_IN,

  @EnumItem(ordinal = 3)
  ITEM_PURCHASE
}
