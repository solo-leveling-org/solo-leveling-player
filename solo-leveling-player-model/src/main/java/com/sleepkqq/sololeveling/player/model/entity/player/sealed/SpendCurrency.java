package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;

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
}
