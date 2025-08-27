package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.model.entity.task.Task;
import java.time.LocalDateTime;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.KeyUniqueConstraint;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "player_tasks")
@KeyUniqueConstraint
public interface PlayerTask extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  PlayerTaskStatus status();

  @Column(name = "_order")
  int order();

  @Nullable
  LocalDateTime closedAt();

  @ManyToOne
  @JoinColumn(name = "player_id")
  Player player();

  @ManyToOne
  @JoinColumn(name = "task_id")
  Task task();
}
