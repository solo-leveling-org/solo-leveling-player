package com.sleepkqq.sololeveling.player.model.entity.player.enums;

import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum PlayerBalanceTransactionCause implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  TASK_COMPLETION,

  @EnumItem(ordinal = 1)
  LEVEL_UP,

  @EnumItem(ordinal = 2)
  DAILY_CHECK_IN,

  @EnumItem(ordinal = 3)
  ITEM_PURCHASE
}
