package com.soloist.player.model.entity.gear.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.soloist.player.model.entity.gear.enums.GearItemSet;
import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("Accessory")
public record AccessoryAttributes(
    BigDecimal strengthMultiplier,
    BigDecimal agilityMultiplier,
    BigDecimal intelligenceMultiplier,
    @Nullable GearItemSet gearItemSet
) implements EquippableAttributes {

}
