package com.sleepkqq.sololeveling.player.service.model.task.enums;

import java.util.Map;
import java.util.Set;

public enum TaskTopic {
  PHYSICAL_ACTIVITY, // 0
  MENTAL_HEALTH, // 1
  EDUCATION, // 2
  CREATIVITY, // 3
  SOCIAL_SKILLS, // 4
  HEALTHY_EATING, // 5
  PRODUCTIVITY, // 6
  EXPERIMENTS, // 7
  ECOLOGY, // 8
  TEAMWORK; // 9

  private static final Map<TaskTopic, Set<TaskTopic>> COMPATIBLE_TOPICS = Map.of(
      PHYSICAL_ACTIVITY, Set.of(MENTAL_HEALTH, HEALTHY_EATING, ECOLOGY),
      MENTAL_HEALTH, Set.of(PHYSICAL_ACTIVITY, CREATIVITY, SOCIAL_SKILLS),
      EDUCATION, Set.of(CREATIVITY, PRODUCTIVITY, EXPERIMENTS),
      CREATIVITY, Set.of(EDUCATION, MENTAL_HEALTH, SOCIAL_SKILLS),
      SOCIAL_SKILLS, Set.of(MENTAL_HEALTH, CREATIVITY, TEAMWORK),
      HEALTHY_EATING, Set.of(PHYSICAL_ACTIVITY, ECOLOGY),
      PRODUCTIVITY, Set.of(EDUCATION, TEAMWORK),
      EXPERIMENTS, Set.of(EDUCATION, ECOLOGY),
      ECOLOGY, Set.of(PHYSICAL_ACTIVITY, HEALTHY_EATING, EXPERIMENTS),
      TEAMWORK, Set.of(SOCIAL_SKILLS, PRODUCTIVITY)
  );

  public Set<TaskTopic> getCompatibleTopics() {
    return COMPATIBLE_TOPICS.getOrDefault(this, Set.of());
  }
}
