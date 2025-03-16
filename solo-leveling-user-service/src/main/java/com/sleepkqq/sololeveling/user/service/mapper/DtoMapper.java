package com.sleepkqq.sololeveling.user.service.mapper;

import com.sleepkqq.sololeveling.proto.user.UserInfo;
import com.sleepkqq.sololeveling.user.service.model.User;
import com.sleepkqq.sololeveling.user.service.model.UserTasks;
import java.time.LocalDateTime;
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

  public UserTasks map(com.sleepkqq.sololeveling.proto.user.UserTasks userTasks) {
    return UserTasks.builder()
        .id(userTasks.getId())
        .currentTasks(mapCollection(userTasks.getCurrentTasksList(), this::map))
        .completedTasks(mapCollection(userTasks.getCompletedTasksList(), this::map))
        .skippedTasks(mapCollection(userTasks.getSkippedTasksList(), this::map))
        .maxTasks(userTasks.getMaxTasks())
        .build();
  }

  public com.sleepkqq.sololeveling.proto.user.UserTasks map(UserTasks userTasks) {
    return com.sleepkqq.sololeveling.proto.user.UserTasks.newBuilder()
        .setId(userTasks.getId())
        .addAllCurrentTasks(mapCollection(userTasks.getCurrentTasks(), this::map))
        .addAllCompletedTasks(mapCollection(userTasks.getCompletedTasks(), this::map))
        .addAllSkippedTasks(mapCollection(userTasks.getSkippedTasks(), this::map))
        .setMaxTasks(userTasks.getMaxTasks())
        .build();
  }

  public UUID map(String string) {
    return UUID.fromString(string);
  }

  public String map(UUID uuid) {
    return uuid.toString();
  }
}
