package com.sleepkqq.sololeveling.player.model.entity.task.enums;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum TaskRarity implements LocalizableEnum {
  @EnumItem(ordinal = 0)
  COMMON,

  @EnumItem(ordinal = 1)
  UNCOMMON,

  @EnumItem(ordinal = 2)
  RARE,

  @EnumItem(ordinal = 3)
  EPIC,

  @EnumItem(ordinal = 4)
  LEGENDARY;

  private final String path = EnumPathGenerator.generatePath(this);
}
