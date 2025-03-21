package com.sleepkqq.sololeveling.user.service.kafka;

import static com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds.TASK_GROUP_ID;
import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.SAVE_TASKS_TOPIC;

import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent;
import com.sleepkqq.sololeveling.user.service.mapper.DtoMapper;
import com.sleepkqq.sololeveling.user.service.model.Task;
import com.sleepkqq.sololeveling.user.service.service.TaskService;
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
  private final DtoMapper dtoMapper;

  @KafkaListener(
      topics = SAVE_TASKS_TOPIC,
      groupId = TASK_GROUP_ID,
      containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
  )
  public void listen(SaveTasksEvent event) {
    log.info(">> Start saving tasks | transactionId={}", event.getTransactionId());
    var taskIds = StreamEx.of(event.getTasks())
        .map(t -> taskService.save(Task.builder()
            .title(t.getTitle())
            .description(t.getDescription())
            .experience(t.getExperience())
            .rarity(t.getRarity())
            .topics(t.getTopics())
            .agility(t.getAgility())
            .strength(t.getStrength())
            .intelligence(t.getIntelligence())
            .build()
        ))
        .map(Task::getId)
        .toList();

    log.info("<< Tasks successfully saved | transactionId={}", event.getTransactionId());
  }
}
