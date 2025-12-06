package com.sleepkqq.sololeveling.player.model.entity.task.enums;

import static java.util.Map.entry;

import com.sleepkqq.sololeveling.jimmer.enums.EnumPathGenerator;
import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.babyfish.jimmer.sql.EnumItem;

@Getter
@RequiredArgsConstructor
public enum TaskTopic implements LocalizableEnum {

  @EnumItem(ordinal = 0)
  PHYSICAL_ACTIVITY(false),

  @EnumItem(ordinal = 1)
  CREATIVITY(false),

  @EnumItem(ordinal = 2)
  SOCIAL_SKILLS(false),

  @EnumItem(ordinal = 3)
  NUTRITION(false),

  @EnumItem(ordinal = 4)
  PRODUCTIVITY(false),

  @EnumItem(ordinal = 5)
  ADVENTURE(false),

  @EnumItem(ordinal = 6)
  MUSIC(false),

  @EnumItem(ordinal = 7)
  BRAIN(false),

  @EnumItem(ordinal = 8)
  CYBERSPORT(true),

  @EnumItem(ordinal = 9)
  DEVELOPMENT(true),

  @EnumItem(ordinal = 10)
  READING(true),

  @EnumItem(ordinal = 11)
  LANGUAGE_LEARNING(true);

  private static final Map<TaskTopic, Set<TaskTopic>> COMPATIBLE_TOPICS = Map.ofEntries(
      entry(PHYSICAL_ACTIVITY, Set.of(ADVENTURE, SOCIAL_SKILLS, MUSIC)),
      entry(CREATIVITY, Set.of(NUTRITION, BRAIN)),
      entry(
          SOCIAL_SKILLS,
          Set.of(PHYSICAL_ACTIVITY, ADVENTURE, MUSIC, CYBERSPORT, READING, LANGUAGE_LEARNING)
      ),
      entry(NUTRITION, Set.of(CREATIVITY)),
      entry(PRODUCTIVITY, Set.of(DEVELOPMENT, READING, LANGUAGE_LEARNING)),
      entry(ADVENTURE, Set.of(PHYSICAL_ACTIVITY, SOCIAL_SKILLS)),
      entry(MUSIC, Set.of(SOCIAL_SKILLS, PHYSICAL_ACTIVITY)),
      entry(BRAIN, Set.of(CREATIVITY, READING, LANGUAGE_LEARNING, CYBERSPORT)),
      entry(CYBERSPORT, Set.of(SOCIAL_SKILLS, BRAIN)),
      entry(DEVELOPMENT, Set.of(PRODUCTIVITY)),
      entry(READING, Set.of(SOCIAL_SKILLS, PRODUCTIVITY, BRAIN)),
      entry(LANGUAGE_LEARNING, Set.of(BRAIN, READING, PRODUCTIVITY, SOCIAL_SKILLS))
  );

  private final boolean isDisabled;
  private final String path = EnumPathGenerator.generatePath(this);

  public Set<TaskTopic> getCompatibleTopics() {
    return COMPATIBLE_TOPICS.getOrDefault(this, Set.of());
  }

  public static Set<TaskTopic> getDisabledTopics() {
    return StreamEx.of(values()).filter(TaskTopic::isDisabled).toSet();
  }
}
