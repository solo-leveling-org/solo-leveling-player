package com.sleepkqq.sololeveling.player.service.kafka;

import static com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds.TASK_GROUP_ID;
import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.SAVE_TASKS_TOPIC;

import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent;
import com.sleepkqq.sololeveling.player.service.mapper.DtoMapper;
import com.sleepkqq.sololeveling.player.service.service.PlayerService;
import com.sleepkqq.sololeveling.player.service.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
  public void listen(SaveTasksEvent event) {
    log.info(">> Start saving tasks | transactionId={}", event.getTransactionId());
    StreamEx.of(event.getTasks())
        .map(t -> taskService.save(dtoMapper.map(t)).getId())
        .forEach(taskId -> {
          var playerTask = playerService.getTask(event.getUserId(), taskId);
          playerTask.inProgress();
          playerService.saveTask(playerTask);
        });

    log.info("<< Tasks successfully saved | transactionId={}", event.getTransactionId());
  }
}
