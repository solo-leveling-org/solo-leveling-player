package com.soloist.player.model.entity.gear;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.gear.enums.GearItemType;
import com.soloist.player.model.entity.localization.LocalizationItem;
import com.soloist.player.model.entity.player.PlayerGearItem;
import com.soloist.player.model.entity.player.enums.Rarity;
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
public interface GearItem extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @OneToOne
  @JoinColumn(name = "localized_title_id")
  LocalizationItem title();

  @OneToOne
  @JoinColumn(name = "localized_description_id")
  LocalizationItem description();

  GearItemType type();

  Rarity rarity();

  int strength();

  int agility();

  int intelligence();

  @OneToMany(mappedBy = "gearItem")
  List<PlayerGearItem> playerGearItems();
}
