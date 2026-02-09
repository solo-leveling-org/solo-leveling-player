package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;

@JsonTypeName("CompleteTasks")
public record CompleteTasks() implements DailyTaskSpec {

  @Override
  public BigDecimal goal() {
    return BigDecimal.TWO;
  }
}
