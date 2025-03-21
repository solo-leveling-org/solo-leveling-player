package com.sleepkqq.sololeveling.user.service.mapper;

import com.google.protobuf.Timestamp;
import com.sleepkqq.sololeveling.proto.user.UserInfo;
import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.model.PlayerTask;
import com.sleepkqq.sololeveling.user.service.model.Player;
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

  public Player map(com.sleepkqq.sololeveling.proto.user.UserTasks userTasks) {
    return Player.builder()
        .id(userTasks.getId())
        .taskTopics(userTasks.getTaskTopicList())
        .maxTasks(userTasks.getMaxTasks())
        .build();
  }

  public com.sleepkqq.sololeveling.proto.user.UserTasks map(Player player) {
    return com.sleepkqq.sololeveling.proto.user.UserTasks.newBuilder()
        .setId(player.getId())
        .addAllTaskTopic(player.getTaskTopics())
        .setMaxTasks(player.getMaxTasks())
        .build();
  }

  public UUID map(String string) {
    return UUID.fromString(string);
  }

  public String map(UUID uuid) {
    return uuid.toString();
  }

  public PlayerTask map(com.sleepkqq.sololeveling.proto.user.UserTaskInfo userTaskInfo) {
    return PlayerTask.builder()
        .id(map(userTaskInfo.getId()))
        .status(userTaskInfo.getStatus())
        .createdAt(map(userTaskInfo.getCreatedAt()))
        .closedAt(map(userTaskInfo.getClosedAt()))
        .build();
  }

  public com.sleepkqq.sololeveling.proto.user.UserTaskInfo map(PlayerTask playerTask) {
    return com.sleepkqq.sololeveling.proto.user.UserTaskInfo.newBuilder()
        .setId(map(playerTask.getId()))
        .setStatus(playerTask.getStatus())
        .setCreatedAt(map(playerTask.getCreatedAt()))
        .setClosedAt(map(playerTask.getClosedAt()))
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
}
