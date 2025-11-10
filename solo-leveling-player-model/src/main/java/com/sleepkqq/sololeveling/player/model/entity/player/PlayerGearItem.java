package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.gear.GearItem;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerGearItemStatus;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
@Table(name = "player_gear_items")
public interface PlayerGearItem {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @ManyToOne
  @JoinColumn(name = "player_id")
  Player player();

  @ManyToOne
  @JoinColumn(name = "gear_item_id")
  GearItem gearItem();

  PlayerGearItemStatus status();
}
