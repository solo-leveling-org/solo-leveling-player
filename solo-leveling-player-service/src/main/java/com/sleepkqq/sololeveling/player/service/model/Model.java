package com.sleepkqq.sololeveling.player.service.model;

import java.time.LocalDateTime;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.babyfish.jimmer.sql.Version;
import org.jetbrains.annotations.NotNull;

@MappedSuperclass
public interface Model {

  @NotNull
  @Column(name = "created_at")
  LocalDateTime createdAt();

  @NotNull
  @Column(name = "updated_at")
  LocalDateTime updatedAt();

  @Version
  @Column(name = "version")
  int version();
}
