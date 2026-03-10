package com.soloist.player.model.entity.player;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.gear.enums.ConsumableType;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
public interface PlayerConsumable extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @ManyToOne
  @JoinColumn(name = "inventory_id")
  Inventory inventory();

  ConsumableType consumableType();

  int quantity();
}
