package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

interface CurrencySpec extends DailyTaskSpec {

  @Override
  default DailyTaskType type() {
    return DailyTaskType.CURRENCY;
  }
}
