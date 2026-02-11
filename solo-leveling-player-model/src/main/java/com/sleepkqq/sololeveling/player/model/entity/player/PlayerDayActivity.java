package com.sleepkqq.sololeveling.player.model.entity.player;

import java.time.LocalDate;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.KeyUniqueConstraint;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@KeyUniqueConstraint
@Entity
@Table(name = "player_day_activities")
public interface PlayerDayActivity {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @Key
  LocalDate day();

  boolean dailyTaskCompleted();

  @Key
  @ManyToOne
  @JoinColumn(name = "player_id")
  Player player();
}
