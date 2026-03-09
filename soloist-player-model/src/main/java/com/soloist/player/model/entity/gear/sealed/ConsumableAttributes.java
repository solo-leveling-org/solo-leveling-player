package com.soloist.player.model.entity.gear.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.soloist.player.model.entity.gear.enums.ConsumableEffect;

@JsonTypeName("Consumable")
public record ConsumableAttributes(
    ConsumableEffect effect
) implements GearItemAttributes {

}
