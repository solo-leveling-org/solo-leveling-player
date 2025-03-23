package com.sleepkqq.sololeveling.player.service.service.player;

import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import com.sleepkqq.sololeveling.player.service.repository.player.PlayerTaskTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerTaskTopicService {

  private final PlayerTaskTopicRepository playerTaskTopicRepository;
  private final LevelService levelService;

  public PlayerTaskTopic initialize(Player player, TaskTopic taskTopic) {
    var topic = PlayerTaskTopic.builder()
        .taskTopic(taskTopic)
        .player(player)
        .build();

    levelService.initializeTopicLevel(topic);

    return playerTaskTopicRepository.save(topic);
  }
}
