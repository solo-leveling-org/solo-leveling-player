package com.sleepkqq.sololeveling.player.service.model.player;

import com.sleepkqq.sololeveling.player.service.model.Model;
import com.sleepkqq.sololeveling.player.service.model.player.enums.Assessment;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "levels")
public interface Level extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @Column(name = "level")
  int level();

  @Column(name = "total_experience")
  int totalExperience();

  @Column(name = "current_experience")
  int currentExperience();

  @Column(name = "experience_to_next_level")
  int experienceToNextLevel();

  @NotNull
  @Column(name = "assessment")
  Assessment assessment();

  @Nullable
  @OneToOne
  @JoinColumn(name = "player_id")
  Player player();

  @Nullable
  @OneToOne
  @JoinColumn(name = "player_task_topic_id")
  PlayerTaskTopic playerTaskTopic();
}
