package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.List;

@JsonTypeName("SpendCurrency")
public record SpendCurrency() implements DailyTaskSpec {

  @Override
  public BigDecimal goal() {
    return BigDecimal.valueOf(1_000);
  }

  @Override
  public String localizationPath() {
    return "spend-currency";
  }

  @Override
  public List<Object> localizationArgs() {
    return List.of(goal());
  }
}
