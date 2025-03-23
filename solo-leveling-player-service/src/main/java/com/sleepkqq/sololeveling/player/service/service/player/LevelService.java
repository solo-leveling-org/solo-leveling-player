package com.sleepkqq.sololeveling.player.service.service.player;

import static com.sleepkqq.sololeveling.player.service.model.player.enums.Assessment.E;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.player.Level;
import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.repository.player.LevelRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelService {

  private static final int BASE_FIRST_LEVEL = 1;
  private static final int BASE_BEGIN_EXPERIENCE = 0;

  private final LevelRepository levelRepository;
  private final CountExperienceService countExperienceService;

  public Level get(UUID id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(Level.class, id));
  }

  public Optional<Level> find(UUID id) {
    return levelRepository.findById(id);
  }

  public Level initializePlayerLevel(Player player) {
    var level = initializeBaseLevel()
        .experienceToNextLevel(
            countExperienceService.countPlayerExperienceToNextLevel(BASE_FIRST_LEVEL)
        )
        .player(player)
        .build();

    level.setPlayer(player);
    return level;
  }

  public Level initializeTopicLevel(PlayerTaskTopic topic) {
    var level = initializeBaseLevel()
        .experienceToNextLevel(
            countExperienceService.countTopicExperienceToNextLevel(BASE_FIRST_LEVEL)
        )
        .playerTaskTopic(topic)
        .build();

    topic.setLevel(level);
    return level;
  }

  private Level.LevelBuilder initializeBaseLevel() {
    return Level.builder()
        .level(BASE_FIRST_LEVEL)
        .totalExperience(BASE_BEGIN_EXPERIENCE)
        .currentExperience(BASE_BEGIN_EXPERIENCE)
        .assessment(E);
  }
}
