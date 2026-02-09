package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CompleteTasks.class, name = "CompleteTasks"),
    @JsonSubTypes.Type(value = CompleteSpecifiedRarityTask.class, name = "CompleteSpecifiedRarityTask"),
    @JsonSubTypes.Type(value = SpendCurrency.class, name = "SpendCurrency")
})
public interface DailyTaskSpec {

  /**
   * Целевое значение для выполнения задачи
   */
  BigDecimal goal();

  DailyTaskType type();

  enum DailyTaskType {
    TASKS,
    CURRENCY
  }
}
