package com.soloist.player.model.entity.player;

import com.soloist.player.model.entity.Model;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
public interface Inventory extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @OneToOne
  @JoinColumn(name = "player_id")
  Player player();

  int gearItemCapacity();

  int consumableCapacity();

  @OneToMany(mappedBy = "inventory")
  List<PlayerGearItem> gearItems();

  @OneToMany(mappedBy = "inventory")
  List<PlayerConsumable> consumables();
}
