package com.sleepkqq.sololeveling.player.service.mapper;

import com.google.protobuf.Timestamp;
import com.sleepkqq.sololeveling.avro.task.SaveTask;
import com.sleepkqq.sololeveling.proto.player.LevelInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerTaskInfo;
import com.sleepkqq.sololeveling.proto.player.PlayerTaskTopicInfo;
import com.sleepkqq.sololeveling.proto.player.TaskInfo;
import com.sleepkqq.sololeveling.proto.player.TaskRarity;
import com.sleepkqq.sololeveling.proto.player.TaskTopic;
import com.sleepkqq.sololeveling.proto.user.UserInfo;
import com.sleepkqq.sololeveling.player.service.model.Level;
import com.sleepkqq.sololeveling.player.service.model.Player;
import com.sleepkqq.sololeveling.player.service.model.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.PlayerTaskTopic;
import com.sleepkqq.sololeveling.player.service.model.Task;
import com.sleepkqq.sololeveling.player.service.model.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

  public <T, R> List<R> mapCollection(Collection<T> collection, Function<T, R> mapper) {
    return StreamEx.of(collection).map(mapper).toList();
  }

  public UserInfo map(User user) {
    return UserInfo.newBuilder()
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setFirstName(user.getFirstName())
        .setLastName(user.getLastName())
        .setPhotoUrl(user.getPhotoUrl())
        .setLocale(user.getLocale().toLanguageTag())
        .addAllRole(user.getRoles())
        .build();
  }

  public User map(UserInfo userInfo) {
    return User.builder()
        .id(userInfo.getId())
        .username(userInfo.getUsername())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .photoUrl(userInfo.getPhotoUrl())
        .locale(Locale.forLanguageTag(userInfo.getLocale()))
        .roles(userInfo.getRoleList())
        .lastLoginAt(LocalDateTime.now())
        .build();
  }

  public Level map(LevelInfo levelInfo) {
    return Level.builder()
        .id(map(levelInfo.getId()))
        .level(levelInfo.getLevel())
        .totalExperience(levelInfo.getTotalExperience())
        .currentExperience(levelInfo.getCurrentExperience())
        .experienceToNextLevel(levelInfo.getExperienceToNextLevel())
        .coefficient(levelInfo.getCoefficient())
        .build();
  }

  public LevelInfo map(Level level) {
    return LevelInfo.newBuilder()
        .setId(map(level.getId()))
        .setLevel(level.getLevel())
        .setTotalExperience(level.getTotalExperience())
        .setCurrentExperience(level.getCurrentExperience())
        .setExperienceToNextLevel(level.getExperienceToNextLevel())
        .setCoefficient(level.getCoefficient())
        .build();
  }

  public Player map(PlayerInfo playerInfo) {
    return Player.builder()
        .id(playerInfo.getId())
        .maxTasks(playerInfo.getMaxTasks())
        .level(map(playerInfo.getLevelInfo()))
        .taskTopics(mapCollection(playerInfo.getPlayerTaskTopicInfoList(), this::map))
        .build();
  }

  public PlayerInfo map(Player player) {
    return PlayerInfo.newBuilder()
        .setId(player.getId())
        .setMaxTasks(player.getMaxTasks())
        .setLevelInfo(map(player.getLevel()))
        .addAllPlayerTaskTopicInfo(mapCollection(player.getTaskTopics(), this::map))
        .build();
  }

  public PlayerTaskTopic map(PlayerTaskTopicInfo playerTaskTopicInfo) {
    return PlayerTaskTopic.builder()
        .taskTopic(playerTaskTopicInfo.getTaskTopic())
        .assessment(playerTaskTopicInfo.getAssessment())
        .completedTasksCount(playerTaskTopicInfo.getCompletedTasksCount())
        .skippedTasksCount(playerTaskTopicInfo.getSkippedTasksCount())
        .level(map(playerTaskTopicInfo.getLevelInfo()))
        .build();
  }

  public PlayerTaskTopicInfo map(PlayerTaskTopic playerTaskTopic) {
    return PlayerTaskTopicInfo.newBuilder()
        .setId(map(playerTaskTopic.getId()))
        .setTaskTopic(playerTaskTopic.getTaskTopic())
        .setAssessment(playerTaskTopic.getAssessment())
        .setCompletedTasksCount(playerTaskTopic.getCompletedTasksCount())
        .setSkippedTasksCount(playerTaskTopic.getSkippedTasksCount())
        .setLevelInfo(map(playerTaskTopic.getLevel()))
        .build();
  }

  public UUID map(String string) {
    return UUID.fromString(string);
  }

  public String map(UUID uuid) {
    return uuid.toString();
  }

  public PlayerTask map(PlayerTaskInfo playerTaskInfo) {
    return PlayerTask.builder()
        .id(map(playerTaskInfo.getId()))
        .task(map(playerTaskInfo.getTaskInfo()))
        .status(playerTaskInfo.getStatus())
        .createdAt(map(playerTaskInfo.getCreatedAt()))
        .closedAt(map(playerTaskInfo.getClosedAt()))
        .build();
  }

  public PlayerTaskInfo map(PlayerTask playerTask) {
    return PlayerTaskInfo.newBuilder()
        .setId(map(playerTask.getId()))
        .setTaskInfo(map(playerTask.getTask()))
        .setStatus(playerTask.getStatus())
        .setCreatedAt(map(playerTask.getCreatedAt()))
        .setClosedAt(map(playerTask.getClosedAt()))
        .build();
  }

  public TaskInfo map(Task task) {
    return TaskInfo.newBuilder()
        .setId(task.getId().toString())
        .setTitle(task.getTitle())
        .setDescription(task.getDescription())
        .setExperience(task.getExperience())
        .setRarity(task.getRarity())
        .setAgility(task.getAgility())
        .setStrength(task.getStrength())
        .setIntelligence(task.getIntelligence())
        .build();
  }

  public Task map(TaskInfo taskInfo) {
    return Task.builder()
        .id(map(taskInfo.getId()))
        .title(taskInfo.getTitle())
        .description(taskInfo.getDescription())
        .experience(taskInfo.getExperience())
        .rarity(taskInfo.getRarity())
        .agility(taskInfo.getAgility())
        .strength(taskInfo.getStrength())
        .intelligence(taskInfo.getIntelligence())
        .build();
  }

  public LocalDateTime map(Timestamp timestamp) {
    var instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  public Timestamp map(LocalDateTime localDateTime) {
    var instant = localDateTime.toInstant(ZoneOffset.UTC);
    return Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();
  }

  public TaskRarity map(com.sleepkqq.sololeveling.avro.task.TaskRarity taskRarity) {
    return TaskRarity.valueOf(taskRarity.name());
  }

  public com.sleepkqq.sololeveling.avro.task.TaskRarity map(TaskRarity taskRarity) {
    return com.sleepkqq.sololeveling.avro.task.TaskRarity.valueOf(taskRarity.name());
  }

  public TaskTopic map(com.sleepkqq.sololeveling.avro.task.TaskTopic taskTopic) {
    return TaskTopic.valueOf(taskTopic.name());
  }

  public com.sleepkqq.sololeveling.avro.task.TaskTopic map(TaskTopic taskTopic) {
    return com.sleepkqq.sololeveling.avro.task.TaskTopic.valueOf(taskTopic.name());
  }

  public Task map(SaveTask saveTask) {
    return Task.builder()
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
}
