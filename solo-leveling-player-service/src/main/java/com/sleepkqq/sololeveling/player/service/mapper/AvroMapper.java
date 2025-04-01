package com.sleepkqq.sololeveling.player.service.mapper;

import com.sleepkqq.sololeveling.avro.task.SaveTask;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import com.sleepkqq.sololeveling.player.service.model.task.TaskDraft;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskRarity;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import org.springframework.stereotype.Component;

@Component
public class AvroMapper extends BaseMapper {

  public Task map(SaveTask saveTask) {
    return new TaskDraft.Builder()
        .id(map(saveTask.getTaskId()))
        .title(saveTask.getTitle())
        .description(saveTask.getDescription())
        .experience(saveTask.getExperience())
        .rarity(map(saveTask.getRarity()))
        .topics(mapCollection(saveTask.getTopics(), this::map))
        .agility(saveTask.getAgility())
        .strength(saveTask.getStrength())
        .intelligence(saveTask.getIntelligence())
        .build();
  }

  public TaskRarity map(com.sleepkqq.sololeveling.avro.task.TaskRarity taskRarity) {
    if (taskRarity == null) {
      return null;
    }
    return TaskRarity.valueOf(taskRarity.name());
  }

  public TaskTopic map(com.sleepkqq.sololeveling.avro.task.TaskTopic topic) {
    if (topic == null) {
      return null;
    }
    return TaskTopic.valueOf(topic.name());
  }

  public com.sleepkqq.sololeveling.avro.task.TaskRarity map(TaskRarity taskRarity) {
    if (taskRarity == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.avro.task.TaskRarity.valueOf(taskRarity.name());
  }

  public com.sleepkqq.sololeveling.avro.task.TaskTopic map(TaskTopic topic) {
    if (topic == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.avro.task.TaskTopic.valueOf(topic.name());
  }
}
