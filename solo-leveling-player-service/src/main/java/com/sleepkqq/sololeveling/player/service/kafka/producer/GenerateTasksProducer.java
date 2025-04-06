package com.sleepkqq.sololeveling.player.service.kafka.producer;

import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.GENERATE_TASKS_TOPIC;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PREPARING;
import static java.lang.String.format;

import com.sleepkqq.sololeveling.avro.task.GenerateTask;
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent;
import com.sleepkqq.sololeveling.player.service.mapper.AvroMapper;
import com.sleepkqq.sololeveling.player.service.model.Immutables;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService;
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskRarityService;
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskTopicService;
import com.sleepkqq.sololeveling.player.service.service.task.TaskService;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTasksProducer {

  private final KafkaTemplate<String, GenerateTasksEvent> kafkaTemplate;
  private final DefineTaskTopicService defineTaskTopicService;
  private final DefineTaskRarityService defineTaskRarityService;
  private final PlayerService playerService;
  private final PlayerTaskService playerTaskService;
  private final TaskService taskService;
  private final AvroMapper avroMapper;

  @Transactional
  public void send(long playerId) {
    var player = playerService.get(playerId);
    var tasksCount = player.maxTasks() - playerTaskService.getCurrentTasksCount(playerId);
    if (tasksCount < 1 || tasksCount > player.maxTasks()) {
      throw new IllegalArgumentException(format(
          "Incorrect current tasks count=%d, playerId=%d", tasksCount, playerId
      ));
    }

    var generateTasks = StreamEx.generate(UUID::randomUUID)
        .limit(tasksCount)
        .peek(taskId -> createEmptyPlayerTask(playerId, taskId))
        .map(taskId -> generateTask(player.taskTopics(), taskId))
        .toList();
    var event = GenerateTasksEvent.newBuilder()
        .setTransactionId(UUID.randomUUID().toString())
        .setPlayerId(playerId)
        .setInputs(generateTasks)
        .build();

    kafkaTemplate.send(GENERATE_TASKS_TOPIC, event);
    log.info("<< Generate tasks event sent | transactionId={}", event.getTransactionId());
  }

  private GenerateTask generateTask(Collection<PlayerTaskTopic> playerTaskTopics, UUID taskId) {
    var taskTopicsMap = StreamEx.of(playerTaskTopics)
        .toMap(PlayerTaskTopic::taskTopic, Function.identity());
    var topics = defineTaskTopicService.define(taskTopicsMap.keySet());
    return new GenerateTask(
        taskId.toString(),
        avroMapper.map(defineTaskRarityService.define(
            StreamEx.of(topics).map(taskTopicsMap::get).toSet()
        )),
        avroMapper.mapCollection(topics, avroMapper::map)
    );
  }

  private void createEmptyPlayerTask(long playerId, UUID taskId) {
    taskService.create(
        Immutables.createTask(t -> {
          t.setId(taskId);
          t.addIntoPlayerTasks(p -> {
            p.setId(UUID.randomUUID());
            p.setStatus(PREPARING);
            p.setPlayerId(playerId);
          });
        })
    );
  }
}
