package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Assessment;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "levels")
public interface Level extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  int level();

  int totalExperience();

  int currentExperience();

  int experienceToNextLevel();

  Assessment assessment();

  @Nullable
  @OneToOne
  @JoinColumn(name = "player_id")
  Player player();

  @OneToOne
  @JoinColumn(name = "player_task_topic_id")
  PlayerTaskTopic playerTaskTopic();
}
