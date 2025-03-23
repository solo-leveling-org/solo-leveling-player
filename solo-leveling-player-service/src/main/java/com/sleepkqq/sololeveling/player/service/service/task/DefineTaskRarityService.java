package com.sleepkqq.sololeveling.player.service.service.task;

import static com.sleepkqq.sololeveling.player.service.model.task.enums.TaskRarity.COMMON;

import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskRarity;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import one.util.streamex.DoubleStreamEx;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefineTaskRarityService {

  private static final Random RANDOM = new Random();

  private static final double COMMON_BASE_WEIGHT = 80.0;
  private static final double UNCOMMON_BASE_WEIGHT = 15.0;
  private static final double RARE_BASE_WEIGHT = 5.0;

  private static final double EPIC_LEVEL_THRESHOLD = 40.0;
  private static final double LEGENDARY_LEVEL_THRESHOLD = 60.0;
  private static final double COMMON_DISABLE_LEVEL_THRESHOLD = 80.0;

  private static final double UNCOMMON_WEIGHT_MULTIPLIER = 0.5;
  private static final double RARE_WEIGHT_MULTIPLIER = 0.3;
  private static final double EPIC_WEIGHT_MULTIPLIER = 0.2;
  private static final double LEGENDARY_WEIGHT_MULTIPLIER = 0.1;

  public TaskRarity define(Set<PlayerTaskTopic> topics) {
    var avgLevel = StreamEx.of(topics)
        .mapToInt(t -> t.getLevel().getLevel())
        .average()
        .orElseThrow(() -> new IllegalArgumentException("Incorrect topics size=" + topics.size()));

    return define(avgLevel);
  }

  private TaskRarity define(double avgLevel) {
    var weights = getWeights(avgLevel);

    var totalWeight = DoubleStreamEx.of(weights).sum();
    var normalizedWeights = DoubleStreamEx.of(weights)
        .map(weight -> (weight / totalWeight) * 100)
        .toArray();

    var randomValue = RANDOM.nextDouble() * 100;
    var cumulativeWeight = 0.0;

    for (var i = 0; i < normalizedWeights.length; i++) {
      cumulativeWeight += normalizedWeights[i];
      if (randomValue < cumulativeWeight) {
        return TaskRarity.values()[i];
      }
    }

    return COMMON;
  }

  private double[] getWeights(double avgLevel) {
    var commonWeight = Math.max(0, COMMON_BASE_WEIGHT - avgLevel);
    var uncommonWeight = UNCOMMON_BASE_WEIGHT + (avgLevel * UNCOMMON_WEIGHT_MULTIPLIER);
    var rareWeight = RARE_BASE_WEIGHT + (avgLevel * RARE_WEIGHT_MULTIPLIER);
    var epicWeight = (avgLevel >= EPIC_LEVEL_THRESHOLD) ? (avgLevel * EPIC_WEIGHT_MULTIPLIER) : 0;
    var legendaryWeight = (avgLevel >= LEGENDARY_LEVEL_THRESHOLD) ? (avgLevel * LEGENDARY_WEIGHT_MULTIPLIER) : 0;

    if (avgLevel >= COMMON_DISABLE_LEVEL_THRESHOLD) {
      commonWeight = 0;
    }

    return new double[]{commonWeight, uncommonWeight, rareWeight, epicWeight, legendaryWeight};
  }
}