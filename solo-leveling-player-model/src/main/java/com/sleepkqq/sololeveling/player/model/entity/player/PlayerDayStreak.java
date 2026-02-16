package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
@Table(name = "player_day_streak")
public interface PlayerDayStreak extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  int current();

  int max();

  @OneToOne
  @JoinColumn(name = "player_id")
  Player player();
}
