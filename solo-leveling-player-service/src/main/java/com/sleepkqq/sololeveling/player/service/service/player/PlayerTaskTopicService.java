package com.sleepkqq.sololeveling.player.service.service.player;

import com.sleepkqq.sololeveling.player.service.model.Immutables;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import com.sleepkqq.sololeveling.player.service.repository.player.PlayerTaskTopicRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerTaskTopicService {

  private final PlayerTaskTopicRepository playerTaskTopicRepository;
  private final LevelService levelService;

  public PlayerTaskTopic initialize(long playerId, TaskTopic taskTopic) {
    return Immutables.createPlayerTaskTopic(playerTaskTopic -> {
      playerTaskTopic.setId(UUID.randomUUID());
      playerTaskTopic.setTaskTopic(taskTopic);
      playerTaskTopic.setPlayerId(playerId);
      playerTaskTopic.applyLevel(levelService::initializeTopicLevel);
    });
  }

  @Transactional
  public PlayerTaskTopic save(PlayerTaskTopic topic) {
    return playerTaskTopicRepository.save(topic);
  }

  @Transactional
  public PlayerTaskTopic update(PlayerTaskTopic topic, LocalDateTime now) {
    return playerTaskTopicRepository.update(Immutables.createPlayerTaskTopic(topic, p ->
        p.setUpdatedAt(now)
    ));
  }

  @Transactional
  public PlayerTaskTopic update(PlayerTaskTopic topic) {
    return update(topic, LocalDateTime.now());
  }
}
