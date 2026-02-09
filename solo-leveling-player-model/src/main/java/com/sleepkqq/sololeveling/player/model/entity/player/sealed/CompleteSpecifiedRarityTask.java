package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity;
import java.math.BigDecimal;

@JsonTypeName("CompleteSpecifiedRarityTask")
public record CompleteSpecifiedRarityTask(Rarity rarity) implements TasksSpec {

  @Override
  public BigDecimal goal() {
    return BigDecimal.ONE;
  }
}
