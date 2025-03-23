package com.sleepkqq.sololeveling.player.service.kafka.consumer;

import static com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds.TASK_GROUP_ID;
import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.SAVE_TASKS_TOPIC;

import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent;
import com.sleepkqq.sololeveling.player.service.mapper.DtoMapper;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService;
import com.sleepkqq.sololeveling.player.service.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveTasksConsumer {

  private final TaskService taskService;
  private final PlayerService playerService;
  private final DtoMapper dtoMapper;

  @KafkaListener(
      topics = SAVE_TASKS_TOPIC,
      groupId = TASK_GROUP_ID,
      containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
  )
  @Transactional
  public void listen(SaveTasksEvent event) {
    log.info(">> Start saving tasks | transactionId={}", event.getTransactionId());
    StreamEx.of(event.getTasks())
        .map(t -> {
          var current = taskService.get(dtoMapper.map(t.getTaskId()));
          var updated = dtoMapper.map(t);
          updated.setVersion(current.getVersion());
          updated.setPlayerTasks(current.getPlayerTasks());
          return taskService.save(updated).getId();
        })
        .forEach(taskId -> {
          var playerTask = playerService.getTask(event.getPlayerId(), taskId);
          playerTask.inProgress();
          playerService.saveTask(playerTask);
        });

    log.info("<< Tasks successfully saved | transactionId={}", event.getTransactionId());
  }
}
