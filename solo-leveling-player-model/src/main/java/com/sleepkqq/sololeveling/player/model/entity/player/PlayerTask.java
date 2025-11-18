package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.model.entity.task.Task;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "player_tasks")
public interface PlayerTask extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  PlayerTaskStatus status();

  @Column(name = "_order")
  int order();

  @ManyToOne
  @JoinColumn(name = "player_id")
  Player player();

  @Nullable
  @ManyToOne
  @JoinColumn(name = "task_id")
  Task task();

  String STATUS_FIELD = "status";
}
