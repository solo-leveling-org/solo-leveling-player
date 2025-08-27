package com.sleepkqq.sololeveling.player.model.entity;

import java.time.LocalDateTime;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.babyfish.jimmer.sql.Version;

@MappedSuperclass
public interface Model {

  LocalDateTime createdAt();

  LocalDateTime updatedAt();

  @Version
  int version();
}
