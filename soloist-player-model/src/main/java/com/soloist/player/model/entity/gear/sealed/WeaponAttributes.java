package com.soloist.player.model.entity.gear.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.soloist.player.model.entity.gear.enums.Element;
import com.soloist.player.model.entity.gear.enums.GearItemSet;
import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("Weapon")
public record WeaponAttributes(
    BigDecimal strengthMultiplier,
    BigDecimal agilityMultiplier,
    BigDecimal intelligenceMultiplier,
    @Nullable GearItemSet gearItemSet,
    BigDecimal damage,
    @Nullable Element element
) implements EquippableAttributes {

}
