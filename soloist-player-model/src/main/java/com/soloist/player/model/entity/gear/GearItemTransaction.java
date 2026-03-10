package com.soloist.player.model.entity.gear;

import com.soloist.player.model.entity.Auditable;
import com.soloist.player.model.entity.gear.enums.GearItemTransactionType;
import com.soloist.player.model.entity.player.Player;
import com.soloist.player.model.entity.player.PlayerGearItem;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
public interface GearItemTransaction extends Auditable {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @ManyToOne
  @JoinColumn(name = "player_gear_item_id")
  PlayerGearItem playerGearItem();

  GearItemTransactionType type();

  @Nullable
  @ManyToOne
  @JoinColumn(name = "from_player_id")
  Player fromPlayer();

  @Nullable
  @ManyToOne
  @JoinColumn(name = "to_player_id")
  Player toPlayer();
}
