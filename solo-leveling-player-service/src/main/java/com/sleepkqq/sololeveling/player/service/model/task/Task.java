package com.sleepkqq.sololeveling.player.service.model.task;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskRarity;
import com.sleepkqq.sololeveling.player.service.model.task.enums.TaskTopic;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.Serialized;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "tasks")
public interface Task extends Model {

  @Id
  UUID id();

  @Nullable
  @Column(name = "title")
  String title();

  @Nullable
  @Column(name = "description")
  String description();

  @Nullable
  @Column(name = "experience")
  Integer experience();

  @Nullable
  @Column(name = "rarity")
  TaskRarity rarity();

  @Nullable
  @Column(name = "agility")
  Integer agility();

  @Nullable
  @Column(name = "strength")
  Integer strength();

  @Nullable
  @Column(name = "intelligence")
  Integer intelligence();

  @Nullable
  @Serialized
  @Column(name = "topics")
  List<TaskTopic> topics();

  @OneToMany(mappedBy = "task")
  List<PlayerTask> playerTasks();
}
