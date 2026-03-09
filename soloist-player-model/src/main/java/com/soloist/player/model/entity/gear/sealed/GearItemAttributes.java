package com.soloist.player.model.entity.gear.sealed;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = WeaponAttributes.class, name = "Weapon"),
    @JsonSubTypes.Type(value = ArmorAttributes.class, name = "Armor"),
    @JsonSubTypes.Type(value = AccessoryAttributes.class, name = "Accessory"),
    @JsonSubTypes.Type(value = ConsumableAttributes.class, name = "Consumable")
})
public interface GearItemAttributes {

}
