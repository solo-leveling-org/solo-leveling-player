package com.soloist.player.model.entity.gear.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.soloist.player.model.entity.gear.enums.GearItemSet;
import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("Armor")
public record ArmorAttributes(
    BigDecimal strengthMultiplier,
    BigDecimal agilityMultiplier,
    BigDecimal intelligenceMultiplier,
    @Nullable GearItemSet gearItemSet
) implements EquippableAttributes {

}
