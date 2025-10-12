package com.sleepkqq.sololeveling.player.model.entity.task.enums;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
public enum TaskTopic implements LocalizableEnum {
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

  private final String path = EnumPathGenerator.generatePath(this);

  public Set<TaskTopic> getCompatibleTopics() {
    return COMPATIBLE_TOPICS.getOrDefault(this, Set.of());
  }
}
