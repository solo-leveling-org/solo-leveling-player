package com.sleepkqq.sololeveling.player.service.mapper;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import one.util.streamex.StreamEx;
import org.springframework.util.CollectionUtils;

abstract class BaseMapper {

  public <T, R> List<R> mapCollection(Collection<T> collection, Function<T, R> mapper) {
    if (CollectionUtils.isEmpty(collection)) {
      return List.of();
    }
    return StreamEx.of(collection).map(mapper).toList();
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

  protected <T> void set(T value, Consumer<T> setter) {
    Optional.ofNullable(value).ifPresent(setter);
  }
}
