package com.soloist.player.model.entity.gear.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConsumableType {

  HEALTH_POTION(ConsumableEffect.FULL_HEALTH_RESTORE),
  MANA_POTION(ConsumableEffect.FULL_MANA_RESTORE),
  STAMINA_POTION(ConsumableEffect.FULL_STAMINA_RESTORE),
  SCROLL_IDENTIFICATION(ConsumableEffect.IDENTIFY_ITEM),
  SCROLL_TELEPORT(ConsumableEffect.TELEPORT),
  ELIXIR_STRENGTH(ConsumableEffect.STRENGTH_BOOST);

  private final ConsumableEffect effect;
}
