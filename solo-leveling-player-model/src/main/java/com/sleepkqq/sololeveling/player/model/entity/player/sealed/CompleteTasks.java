package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.List;

@JsonTypeName("CompleteTasks")
public record CompleteTasks() implements DailyTaskSpec {

  @Override
  public BigDecimal goal() {
    return BigDecimal.TWO;
  }

  @Override
  public String localizationPath() {
    return "complete-tasks";
  }

  @Override
  public List<Object> localizationArgs() {
    return List.of(goal());
  }
}
