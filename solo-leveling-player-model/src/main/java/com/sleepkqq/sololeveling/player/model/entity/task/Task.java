package com.sleepkqq.sololeveling.player.model.entity.task;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask;
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskRarity;
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.KeyUniqueConstraint;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.Serialized;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "tasks")
@KeyUniqueConstraint
public interface Task extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @Nullable
  String title();

  @Nullable
  String description();

  @Nullable
  Integer experience();

  @Nullable
  Integer currencyReward();

  @Nullable
  TaskRarity rarity();

  @Nullable
  Integer agility();

  @Nullable
  Integer strength();

  @Nullable
  Integer intelligence();

  @Nullable
  @Serialized
  Set<TaskTopic> topics();

  @OneToMany(mappedBy = "task")
  List<PlayerTask> playerTasks();
}
