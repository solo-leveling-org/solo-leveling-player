package com.sleepkqq.sololeveling.player.service.model.player;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "player_task_topics")
public interface PlayerTaskTopic extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @NotNull
  @Column(name = "task_topic")
  TaskTopic taskTopic();

  @NotNull
  @ManyToOne
  @JoinColumn(name = "player_id")
  Player player();

  @Nullable
  @OneToOne(mappedBy = "playerTaskTopic")
  Level level();
}