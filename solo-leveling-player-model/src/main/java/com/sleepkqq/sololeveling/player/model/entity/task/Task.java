package com.sleepkqq.sololeveling.player.model.entity.task;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.localization.LocalizationItem;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask;
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "tasks")
public interface Task extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @Nullable
  @OneToOne
  @JoinColumn(name = "localized_title_id")
  LocalizationItem title();

  @Nullable
  @OneToOne
  @JoinColumn(name = "localized_description_id")
  LocalizationItem description();

  @Nullable
  Integer experience();

  @Nullable
  Integer currencyReward();

  @Nullable
  Rarity rarity();

  @Nullable
  Integer agility();

  @Nullable
  Integer strength();

  @Nullable
  Integer intelligence();

  @OneToMany(mappedBy = "task")
  List<TaskTopicItem> topics();

  @OneToMany(mappedBy = "task")
  List<PlayerTask> playerTasks();

  String RARITY_FIELD = "rarity";
}
