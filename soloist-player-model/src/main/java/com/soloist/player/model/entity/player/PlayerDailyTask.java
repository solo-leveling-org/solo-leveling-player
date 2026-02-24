package com.soloist.player.model.entity.player;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.player.enums.DailyTaskType;
import com.soloist.player.model.entity.player.sealed.DailyTaskSpec;
import java.math.BigDecimal;
import java.util.UUID;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Serialized;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
@Table(name = "player_daily_tasks")
public interface PlayerDailyTask extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  BigDecimal progress();

  @Serialized
  DailyTaskSpec spec();

  DailyTaskType type();

  @Column(name = "is_completed")
  boolean completed();

  @ManyToOne
  @JoinColumn(name = "player_id")
  Player player();
}
