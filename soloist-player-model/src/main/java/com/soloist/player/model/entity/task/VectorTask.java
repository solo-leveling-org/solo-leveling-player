package com.soloist.player.model.entity.task;

import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "task_vector_store")
public interface VectorTask {

  @Id
  UUID id();

  String content();

  @Nullable
  String metadata();

  @Nullable
  String embedding();
}
