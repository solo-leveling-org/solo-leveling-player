package com.sleepkqq.sololeveling.player.model.entity.player.sealed;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigDecimal;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CompleteTasks.class, name = "CompleteTasks"),
    @JsonSubTypes.Type(value = SpendCurrency.class, name = "SpendCurrency")
})
public interface DailyTaskSpec {

  String LOCALIZATION_PREFIX = "tasks.daily.";

  /**
   * Целевое значение для выполнения задачи
   */
  BigDecimal goal();

  String localizationPath();

  default List<Object> localizationArgs() {
    return List.of();
  }

  default String fullLocalizationKey() {
    return LOCALIZATION_PREFIX + localizationPath();
  }
}
