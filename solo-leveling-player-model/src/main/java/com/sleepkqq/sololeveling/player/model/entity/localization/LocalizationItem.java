package com.sleepkqq.sololeveling.player.model.entity.localization;

import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
@Table(name = "localization_items")
public interface LocalizationItem {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  String en();

  String ru();
}
