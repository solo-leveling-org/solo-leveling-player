package com.soloist.player.model.entity.gear.enums;

import com.soloist.jimmer.enums.LocalizableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
@RequiredArgsConstructor
public enum GearItemType implements LocalizableEnum {

  @EnumItem(ordinal = 0)
  SWORD(GearItemCategory.WEAPON),

  @EnumItem(ordinal = 1)
  DAGGERS(GearItemCategory.WEAPON),

  @EnumItem(ordinal = 2)
  STAFF(GearItemCategory.WEAPON),

  @EnumItem(ordinal = 3)
  AXE(GearItemCategory.WEAPON),

  @EnumItem(ordinal = 4)
  BOW(GearItemCategory.WEAPON),

  @EnumItem(ordinal = 5)
  BOOTS(GearItemCategory.ARMOR),

  @EnumItem(ordinal = 6)
  PANTS(GearItemCategory.ARMOR),

  @EnumItem(ordinal = 7)
  CHEST(GearItemCategory.ARMOR),

  @EnumItem(ordinal = 8)
  HELMET(GearItemCategory.ARMOR),

  @EnumItem(ordinal = 9)
  CLOAK(GearItemCategory.ARMOR),

  @EnumItem(ordinal = 10)
  GLOVES(GearItemCategory.ARMOR),

  @EnumItem(ordinal = 11)
  RING(GearItemCategory.ACCESSORY),

  @EnumItem(ordinal = 12)
  AMULET(GearItemCategory.ACCESSORY),

  @EnumItem(ordinal = 13)
  BRACELET(GearItemCategory.ACCESSORY),

  @EnumItem(ordinal = 14)
  HEALTH_POTION(GearItemCategory.CONSUMABLE),

  @EnumItem(ordinal = 15)
  MANA_POTION(GearItemCategory.CONSUMABLE),

  @EnumItem(ordinal = 16)
  SCROLL_IDENTIFICATION(GearItemCategory.CONSUMABLE),

  @EnumItem(ordinal = 17)
  SCROLL_TELEPORT(GearItemCategory.CONSUMABLE),

  @EnumItem(ordinal = 18)
  ELIXIR_STRENGTH(GearItemCategory.CONSUMABLE);

  private final GearItemCategory category;
}
