package com.sleepkqq.sololeveling.player.service.model.task.enums;

import java.util.Map;
import java.util.Set;
import org.babyfish.jimmer.sql.EnumItem;

public enum TaskTopic {
  @EnumItem(ordinal = 0)
  PHYSICAL_ACTIVITY,
  @EnumItem(ordinal = 1)
  MENTAL_HEALTH,
  @EnumItem(ordinal = 2)
  EDUCATION,
  @EnumItem(ordinal = 3)
  CREATIVITY,
  @EnumItem(ordinal = 4)
  SOCIAL_SKILLS,
  @EnumItem(ordinal = 5)
  HEALTHY_EATING,
  @EnumItem(ordinal = 6)
  PRODUCTIVITY,
  @EnumItem(ordinal = 7)
  EXPERIMENTS,
  @EnumItem(ordinal = 8)
  ECOLOGY,
  @EnumItem(ordinal = 9)
  TEAMWORK;

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
