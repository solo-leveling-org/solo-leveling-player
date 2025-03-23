package com.sleepkqq.sololeveling.player.service.mapper;

import com.google.protobuf.Timestamp;
import com.sleepkqq.sololeveling.avro.task.SaveTask;
import com.sleepkqq.sololeveling.player.service.model.player.enums.Assessment;
import com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskRarity;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import com.sleepkqq.sololeveling.player.service.model.user.enums.UserRole;
import com.sleepkqq.sololeveling.proto.player.LevelInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerTaskInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicInfo;
import com.sleepkqq.sololeveling.proto.player.TaskInfo;
import com.sleepkqq.sololeveling.proto.user.UserInfo;
import com.sleepkqq.sololeveling.player.service.model.player.Level;
import com.sleepkqq.sololeveling.player.service.model.player.Player;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import com.sleepkqq.sololeveling.player.service.model.user.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

  public <T, R> List<R> mapCollection(Collection<T> collection, Function<T, R> mapper) {
    return StreamEx.of(collection).map(mapper).toList();
  }

  public UserInfo map(User user) {
    if (user == null) {
      return null;
    }
    var builder = UserInfo.newBuilder();
    set(user.getId(), builder::setId);
    set(user.getUsername(), builder::setUsername);
    set(user.getFirstName(), builder::setFirstName);
    set(user.getLastName(), builder::setLastName);
    set(user.getPhotoUrl(), builder::setPhotoUrl);
    set(map(user.getLocale()), builder::setLocale);
    return builder.addAllRole(mapCollection(user.getRoles(), this::map))
        .build();
  }

  public User map(UserInfo userInfo) {
    if (userInfo == null) {
      return null;
    }
    return User.builder()
        .id(userInfo.getId())
        .username(userInfo.getUsername())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .photoUrl(userInfo.getPhotoUrl())
        .locale(Locale.forLanguageTag(userInfo.getLocale()))
        .roles(mapCollection(userInfo.getRoleList(), this::map))
        .lastLoginAt(LocalDateTime.now())
        .build();
  }

  public String map(Locale locale) {
    return locale.toLanguageTag();
  }

  public UserRole map(com.sleepkqq.sololeveling.proto.user.UserRole userRole) {
    if (userRole == null) {
      return null;
    }
    return UserRole.valueOf(userRole.name());
  }

  public com.sleepkqq.sololeveling.proto.user.UserRole map(UserRole userRole) {
    if (userRole == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.user.UserRole.valueOf(userRole.name());
  }

  public Level map(LevelInfo levelInfo) {
    if (levelInfo == null) {
      return null;
    }
    return Level.builder()
        .id(map(levelInfo.getId()))
        .level(levelInfo.getLevel())
        .totalExperience(levelInfo.getTotalExperience())
        .currentExperience(levelInfo.getCurrentExperience())
        .experienceToNextLevel(levelInfo.getExperienceToNextLevel())
        .assessment(map(levelInfo.getAssessment()))
        .build();
  }

  public LevelInfo map(Level level) {
    if (level == null) {
      return null;
    }
    var builder = LevelInfo.newBuilder();
    set(map(level.getId()), builder::setId);
    set(level.getLevel(), builder::setLevel);
    set(level.getTotalExperience(), builder::setTotalExperience);
    set(level.getCurrentExperience(), builder::setCurrentExperience);
    set(level.getExperienceToNextLevel(), builder::setExperienceToNextLevel);
    set(map(level.getAssessment()), builder::setAssessment);
    return builder.build();
  }

  public Player map(PlayerInfo playerInfo) {
    if (playerInfo == null) {
      return null;
    }
    return Player.builder()
        .id(playerInfo.getId())
        .maxTasks(playerInfo.getMaxTasks())
        .level(map(playerInfo.getLevelInfo()))
        .taskTopics(mapCollection(playerInfo.getPlayerTaskTopicInfoList(), this::map))
        .build();
  }

  public PlayerInfo map(Player player) {
    if (player == null) {
      return null;
    }
    var builder = PlayerInfo.newBuilder();
    builder.setId(player.getId());
    set(player.getMaxTasks(), builder::setMaxTasks);
    set(map(player.getLevel()), builder::setLevelInfo);
    return builder.addAllPlayerTaskTopicInfo(mapCollection(player.getTaskTopics(), this::map))
        .build();
  }

  public PlayerTaskTopic map(PlayerTaskTopicInfo playerTaskTopicInfo) {
    if (playerTaskTopicInfo == null) {
      return null;
    }
    return PlayerTaskTopic.builder()
        .taskTopic(map(playerTaskTopicInfo.getTaskTopic()))
        .level(map(playerTaskTopicInfo.getLevelInfo()))
        .build();
  }

  public PlayerTaskTopicInfo map(PlayerTaskTopic playerTaskTopic) {
    if (playerTaskTopic == null) {
      return null;
    }
    var builder = PlayerTaskTopicInfo.newBuilder();
    set(map(playerTaskTopic.getId()), builder::setId);
    set(map(playerTaskTopic.getTaskTopic()), builder::setTaskTopic);
    set(map(playerTaskTopic.getLevel()), builder::setLevelInfo);
    return builder.build();
  }

  public TaskTopic map(com.sleepkqq.sololeveling.proto.player.TaskTopic taskTopic) {
    if (taskTopic == null) {
      return null;
    }
    return TaskTopic.valueOf(taskTopic.name());
  }

  public com.sleepkqq.sololeveling.proto.player.TaskTopic map(TaskTopic taskTopic) {
    if (taskTopic == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.TaskTopic.valueOf(taskTopic.name());
  }

  public Assessment map(com.sleepkqq.sololeveling.proto.player.Assessment assessment) {
    if (assessment == null) {
      return null;
    }
    return Assessment.valueOf(assessment.name());
  }

  public com.sleepkqq.sololeveling.proto.player.Assessment map(Assessment assessment) {
    if (assessment == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.Assessment.valueOf(assessment.name());
  }

  public UUID map(String string) {
    if (string == null) {
      return null;
    }
    return UUID.fromString(string);
  }

  public String map(UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return uuid.toString();
  }

  public PlayerTask map(PlayerTaskInfo playerTaskInfo) {
    if (playerTaskInfo == null) {
      return null;
    }
    return PlayerTask.builder()
        .id(map(playerTaskInfo.getId()))
        .task(map(playerTaskInfo.getTaskInfo()))
        .status(map(playerTaskInfo.getStatus()))
        .createdAt(map(playerTaskInfo.getCreatedAt()))
        .closedAt(map(playerTaskInfo.getClosedAt()))
        .build();
  }

  public PlayerTaskInfo map(PlayerTask playerTask) {
    if (playerTask == null) {
      return null;
    }
    var builder = PlayerTaskInfo.newBuilder();
    set(map(playerTask.getId()), builder::setId);
    set(map(playerTask.getTask()), builder::setTaskInfo);
    set(map(playerTask.getStatus()), builder::setStatus);
    set(map(playerTask.getCreatedAt()), builder::setCreatedAt);
    set(map(playerTask.getClosedAt()), builder::setClosedAt);
    return builder.build();
  }

  public PlayerTaskStatus map(
      com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus playerTaskStatus
  ) {
    if (playerTaskStatus == null) {
      return null;
    }
    return PlayerTaskStatus.valueOf(playerTaskStatus.name());
  }

  public com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus map(
      PlayerTaskStatus playerTaskStatus
  ) {
    if (playerTaskStatus == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.PlayerTaskStatus.valueOf(playerTaskStatus.name());
  }

  public TaskInfo map(Task task) {
    if (task == null) {
      return null;
    }
    var builder = TaskInfo.newBuilder();
    set(map(task.getId()), builder::setId);
    set(task.getTitle(), builder::setTitle);
    set(task.getDescription(), builder::setDescription);
    set(task.getExperience(), builder::setExperience);
    set(map(task.getRarity()), builder::setRarity);
    set(task.getAgility(), builder::setAgility);
    set(task.getStrength(), builder::setStrength);
    set(task.getIntelligence(), builder::setIntelligence);
    return builder.build();
  }

  public Task map(TaskInfo taskInfo) {
    if (taskInfo == null) {
      return null;
    }
    return Task.builder()
        .id(map(taskInfo.getId()))
        .title(taskInfo.getTitle())
        .description(taskInfo.getDescription())
        .experience(taskInfo.getExperience())
        .rarity(map(taskInfo.getRarity()))
        .agility(taskInfo.getAgility())
        .strength(taskInfo.getStrength())
        .intelligence(taskInfo.getIntelligence())
        .build();
  }

  public Task map(SaveTask saveTask) {
    if (saveTask == null) {
      return null;
    }
    return Task.builder()
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

  public LocalDateTime map(Timestamp timestamp) {
    if (timestamp == null) {
      return null;
    }
    var instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  public Timestamp map(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    var instant = localDateTime.toInstant(ZoneOffset.UTC);
    return Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();
  }

  public TaskRarity map(
      com.sleepkqq.sololeveling.avro.task.TaskRarity taskRarity
  ) {
    if (taskRarity == null) {
      return null;
    }
    return TaskRarity.valueOf(taskRarity.name());
  }

  public com.sleepkqq.sololeveling.avro.task.TaskRarity mapToAvro(TaskRarity taskRarity) {
    if (taskRarity == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.avro.task.TaskRarity.valueOf(taskRarity.name());
  }

  public TaskRarity map(
      com.sleepkqq.sololeveling.proto.player.TaskRarity taskRarity
  ) {
    if (taskRarity == null) {
      return null;
    }
    return TaskRarity.valueOf(taskRarity.name());
  }

  public com.sleepkqq.sololeveling.proto.player.TaskRarity map(TaskRarity taskRarity) {
    if (taskRarity == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.proto.player.TaskRarity.valueOf(taskRarity.name());
  }

  public TaskTopic map(
      com.sleepkqq.sololeveling.avro.task.TaskTopic taskTopic
  ) {
    if (taskTopic == null) {
      return null;
    }
    return TaskTopic.valueOf(taskTopic.name());
  }

  public com.sleepkqq.sololeveling.avro.task.TaskTopic mapToAvro(TaskTopic taskTopic) {
    if (taskTopic == null) {
      return null;
    }
    return com.sleepkqq.sololeveling.avro.task.TaskTopic.valueOf(taskTopic.name());
  }

  private <T> void set(T value, Consumer<T> setter) {
    Optional.ofNullable(value).ifPresent(setter);
  }
}
