package com.sleepkqq.sololeveling.player.service.service.player;

import org.springframework.stereotype.Service;

@Service
public class CountExperienceService {

  private static final int FIRST_LEVEL_PLAYER_EXPERIENCE = 100;
  private static final int NEXT_LEVEL_PLAYER_EXPERIENCE = 10;

  private static final int PLAYER_TASK_TOPIC_COEFFICIENT = 5;
  private static final int FIRST_LEVEL_TOPIC_EXPERIENCE =
      FIRST_LEVEL_PLAYER_EXPERIENCE / PLAYER_TASK_TOPIC_COEFFICIENT;
  private static final int NEXT_LEVEL_TOPIC_EXPERIENCE =
      NEXT_LEVEL_PLAYER_EXPERIENCE / PLAYER_TASK_TOPIC_COEFFICIENT;

  public int countPlayerExperienceToNextLevel(int level) {
    return countExperienceToNextLevel(
        level, FIRST_LEVEL_PLAYER_EXPERIENCE, NEXT_LEVEL_PLAYER_EXPERIENCE
    );
  }

  public int countTopicExperienceToNextLevel(int level) {
    return countExperienceToNextLevel(
        level, FIRST_LEVEL_TOPIC_EXPERIENCE, NEXT_LEVEL_TOPIC_EXPERIENCE
    );
  }

  private int countExperienceToNextLevel(
      int level,
      int firstLevelExperience,
      int nextLevelExperience
  ) {
    return firstLevelExperience + (nextLevelExperience * (level - 1));
  }
}
