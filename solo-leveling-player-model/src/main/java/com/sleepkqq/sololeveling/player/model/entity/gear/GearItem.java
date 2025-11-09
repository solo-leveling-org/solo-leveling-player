package com.sleepkqq.sololeveling.player.model.entity.gear;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.gear.enums.GearItemType;
import com.sleepkqq.sololeveling.player.model.entity.task.enums.Rarity;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.KeyUniqueConstraint;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "gear_items")
@KeyUniqueConstraint
public interface GearItem extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  String name();

  @Nullable
  String description();

  GearItemType type();

  Rarity rarity();

  @Nullable
  Integer strength();

  @Nullable
  Integer agility();

  @Nullable
  Integer intelligence();
}
