package com.sleepkqq.sololeveling.player.service.kafka.producer;

import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.GENERATE_TASKS_TOPIC;
import static com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus.PREPARING;
import static java.lang.String.format;

import com.sleepkqq.sololeveling.avro.task.GenerateTask;
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent;
import com.sleepkqq.sololeveling.player.service.mapper.DtoMapper;
import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService;
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskRarityService;
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskTopicService;
import com.sleepkqq.sololeveling.player.service.service.task.TaskService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
  private final TaskService taskService;
  private final DtoMapper dtoMapper;

  @Transactional
  public void send(long playerId) {
    var player = playerService.get(playerId);
    var tasksCount = player.getMaxTasks() - playerService.getCurrentTasks(player.getId()).size();
    if (tasksCount < 1 || tasksCount > 5) {
      throw new IllegalArgumentException(format(
          "Incorrect current tasks count=%d, playerId=%d", tasksCount, player.getId()
      ));
    }

    var taskTopicsMap = StreamEx.of(player.getTaskTopics())
        .toMap(PlayerTaskTopic::getTaskTopic, t -> t);

    var taskIds = new ArrayList<UUID>();

    var event = GenerateTasksEvent.newBuilder()
        .setTransactionId(UUID.randomUUID().toString())
        .setPlayerId(player.getId())
        .setInputs(StreamEx.generate(() -> generateTask(taskTopicsMap, taskIds))
            .limit(tasksCount)
            .toList()
        )
        .build();

    StreamEx.of(taskIds)
        .map(this::saveEmptyTask)
        .forEach(t -> saveEmptyPlayerTask(player, t));

    kafkaTemplate.send(GENERATE_TASKS_TOPIC, event);
    log.info("<< Generate tasks event sent | transactionId={}", event.getTransactionId());
  }

  private GenerateTask generateTask(
      Map<TaskTopic, PlayerTaskTopic> taskTopicsMap,
      List<UUID> taskIds
  ) {
    var taskId = UUID.randomUUID();
    taskIds.add(taskId);
    var topics = defineTaskTopicService.define(taskTopicsMap.keySet());

    return new GenerateTask(
        taskId.toString(),
        dtoMapper.mapToAvro(defineTaskRarityService.define(
            StreamEx.of(topics).map(taskTopicsMap::get).toSet()
        )),
        dtoMapper.mapCollection(topics, dtoMapper::mapToAvro)
    );
  }

  private Task saveEmptyTask(UUID taskId) {
    return taskService.save(Task.builder().id(taskId).build());
  }

  private PlayerTask saveEmptyPlayerTask(Player player, Task task) {
    return playerService.saveTask(
        PlayerTask.builder().task(task).player(player).status(PREPARING).build()
    );
  }
}
