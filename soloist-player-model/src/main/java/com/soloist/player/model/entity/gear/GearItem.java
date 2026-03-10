package com.soloist.player.model.entity.gear;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.gear.enums.Element;
import com.soloist.player.model.entity.gear.enums.GearItemSet;
import com.soloist.player.model.entity.gear.enums.GearItemType;
import com.soloist.player.model.entity.localization.LocalizationItem;
import com.soloist.player.model.entity.player.PlayerGearItem;
import com.soloist.player.model.entity.player.enums.Rarity;
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
public interface GearItem extends Model {

  String RARITY_FIELD = "rarity";
  String TYPE_FIELD = "type";
  String ELEMENT_FIELD = "element";
  String GEAR_SET_FIELD = "gearSet";

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

  @Nullable
  String imageFileId();

  @Nullable
  BigDecimal strengthMultiplier();

  @Nullable
  BigDecimal agilityMultiplier();

  @Nullable
  BigDecimal intelligenceMultiplier();

  @Nullable
  BigDecimal damage();

  @Nullable
  Element element();

  @Nullable
  GearItemSet gearSet();

  @OneToMany(mappedBy = "gearItem")
  List<PlayerGearItem> playerGearItems();
}
