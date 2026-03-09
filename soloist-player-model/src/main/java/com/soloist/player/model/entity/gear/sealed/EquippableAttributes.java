package com.soloist.player.model.entity.gear.sealed;

import com.soloist.player.model.entity.gear.enums.GearItemSet;
import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;

public interface EquippableAttributes extends GearItemAttributes {

  BigDecimal strengthMultiplier();

  BigDecimal agilityMultiplier();

  BigDecimal intelligenceMultiplier();

  @Nullable
  GearItemSet gearItemSet();
}
