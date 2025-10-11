package com.sleepkqq.sololeveling.player.model.entity.player.enums;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum PlayerTaskStatus implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  PREPARING,

  @EnumItem(ordinal = 1)
  IN_PROGRESS,

  @EnumItem(ordinal = 2)
  PENDING_COMPLETION,

  @EnumItem(ordinal = 3)
  COMPLETED,

  @EnumItem(ordinal = 4)
  SKIPPED;

  private final String path = EnumPathGenerator.generatePath(this);
}
