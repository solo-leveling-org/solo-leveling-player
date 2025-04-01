package com.sleepkqq.sololeveling.player.service.kafka.consumer;

import static com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds.TASK_GROUP_ID;
import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.SAVE_TASKS_TOPIC;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.IN_PROGRESS;

import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent;
import com.sleepkqq.sololeveling.player.service.mapper.AvroMapper;
import com.sleepkqq.sololeveling.player.service.model.Immutables;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService;
import com.sleepkqq.sololeveling.player.service.service.task.TaskService;
import java.time.LocalDateTime;
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
  private final PlayerTaskService playerTaskService;
  private final AvroMapper avroMapper;

  @KafkaListener(
      topics = SAVE_TASKS_TOPIC,
      groupId = TASK_GROUP_ID,
      containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
  )
  @Transactional
  public void listen(SaveTasksEvent event) {
    log.info(">> Start saving tasks | transactionId={}", event.getTransactionId());
    var now = LocalDateTime.now();
    StreamEx.of(event.getTasks())
        .map(t -> {
          var task = avroMapper.map(t);
          var version = taskService.getVersion(task.id());
          return taskService.update(
              Immutables.createTask(task, td -> td.setVersion(version)), now
          );
        })
        .forEach(t -> {
          var playerTaskId = playerTaskService.getTaskId(event.getPlayerId(), t.id());
          playerTaskService.setStatus(playerTaskId, IN_PROGRESS, now);
        });

    log.info("<< Tasks successfully saved | transactionId={}", event.getTransactionId());
  }
}
