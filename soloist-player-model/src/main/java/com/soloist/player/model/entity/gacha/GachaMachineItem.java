package com.soloist.player.model.entity.gacha;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.gear.GearItem;
import java.util.UUID;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OnDissociate;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
public interface GachaMachineItem extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @ManyToOne
  @OnDissociate(DissociateAction.DELETE)
  @JoinColumn(name = "gacha_machine_id")
  GachaMachine gachaMachine();

  @ManyToOne
  @JoinColumn(name = "gear_item_id")
  GearItem gearItem();

  int weight();
}
