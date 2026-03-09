package com.soloist.player.model.entity.gacha;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.localization.LocalizationItem;
import com.soloist.player.model.entity.player.enums.CurrencyCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;
import org.jetbrains.annotations.Nullable;

@Entity
public interface GachaMachine extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  @OneToOne
  @JoinColumn(name = "localized_name_id")
  LocalizationItem name();

  @OneToOne
  @JoinColumn(name = "localized_description_id")
  LocalizationItem description();

  BigDecimal costAmount();

  CurrencyCode costCurrencyCode();

  int pullCount();

  boolean isActive();

  @Nullable
  String imageFileId();

  @OneToMany(mappedBy = "gachaMachine")
  List<GachaMachineItem> machineItems();
}
