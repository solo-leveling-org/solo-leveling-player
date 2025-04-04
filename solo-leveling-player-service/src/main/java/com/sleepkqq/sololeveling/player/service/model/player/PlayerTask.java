package com.sleepkqq.sololeveling.player.service.model.player;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import java.time.LocalDateTime;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "player_tasks")
public interface PlayerTask extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @NotNull
  @Column(name = "status")
  PlayerTaskStatus status();

  @Nullable
  @Column(name = "closed_at")
  LocalDateTime closedAt();

  @NotNull
  @ManyToOne
  @JoinColumn(name = "player_id")
  Player player();

  @NotNull
  @ManyToOne
  @JoinColumn(name = "task_id")
  Task task();
}