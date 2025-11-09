package com.sleepkqq.sololeveling.player.model.entity.gear.enums;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum GearItemType implements LocalizableEnum {

  @EnumItem(ordinal = 0)
  SWORD,

  @EnumItem(ordinal = 1)
  DAGGERS,

  @EnumItem(ordinal = 2)
  STAFF,

  @EnumItem(ordinal = 3)
  AXE,

  @EnumItem(ordinal = 4)
  BOW,

  @EnumItem(ordinal = 5)
  BOOTS,

  @EnumItem(ordinal = 6)
  PANTS,

  @EnumItem(ordinal = 7)
  CHEST,

  @EnumItem(ordinal = 8)
  HELMET,

  @EnumItem(ordinal = 9)
  CLOAK,

  @EnumItem(ordinal = 10)
  GLOVES,

  @EnumItem(ordinal = 11)
  RING,

  @EnumItem(ordinal = 12)
  AMULET,

  @EnumItem(ordinal = 13)
  BRACELET;

  private final String path = EnumPathGenerator.generatePath(this);
}
