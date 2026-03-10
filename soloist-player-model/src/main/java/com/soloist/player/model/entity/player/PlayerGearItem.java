package com.soloist.player.model.entity.player;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.gear.GearItem;
import com.soloist.player.model.entity.gear.GearItemTransaction;
import com.soloist.player.model.entity.player.enums.PlayerGearItemStatus;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
public interface PlayerGearItem extends Model {

  String STATUS_FIELD = "status";

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @Nullable
  @ManyToOne
  @JoinColumn(name = "inventory_id")
  Inventory inventory();

  @ManyToOne
  @JoinColumn(name = "gear_item_id")
  GearItem gearItem();

  PlayerGearItemStatus status();

  @OneToMany(mappedBy = "playerGearItem")
  List<GearItemTransaction> transactions();
}
