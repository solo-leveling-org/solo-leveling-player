package com.sleepkqq.sololeveling.player.model.entity.player.enums;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum PlayerBalanceTransactionType implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  IN,

  @EnumItem(ordinal = 1)
  OUT;

  private final String path = EnumPathGenerator.generatePath(this);
}
