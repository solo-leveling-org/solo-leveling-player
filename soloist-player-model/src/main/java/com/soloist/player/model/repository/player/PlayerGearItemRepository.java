package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.PLAYER_GEAR_ITEM_TABLE;
import static com.soloist.player.model.entity.gear.GearItem.ELEMENT_FIELD;
import static com.soloist.player.model.entity.gear.GearItem.GEAR_SET_FIELD;
import static com.soloist.player.model.entity.gear.GearItem.RARITY_FIELD;
import static com.soloist.player.model.entity.gear.GearItem.TYPE_FIELD;
import static com.soloist.player.model.entity.player.PlayerGearItem.STATUS_FIELD;

import com.soloist.jimmer.enums.LocalizableEnum;
import com.soloist.jimmer.fetcher.PageFetcher;
import com.soloist.player.model.entity.gear.enums.Element;
import com.soloist.player.model.entity.gear.enums.GearItemSet;
import com.soloist.player.model.entity.gear.enums.GearItemType;
import com.soloist.player.model.entity.player.PlayerGearItem;
import com.soloist.player.model.entity.player.PlayerGearItemTable;
import com.soloist.player.model.entity.player.dto.PlayerGearItemView;
import com.soloist.player.model.entity.player.enums.PlayerGearItemStatus;
import com.soloist.player.model.entity.player.enums.Rarity;
import com.soloist.proto.common.RequestPaging;
import com.soloist.proto.common.RequestQueryOptions;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.mutation.BatchSaveResult.View.ViewItem;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.ast.table.TableEx;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerGearItemRepository extends PageFetcher<PlayerGearItem, PlayerGearItemTable> {

  public static final Map<String, Class<? extends LocalizableEnum>> FIELD_ENUM_TYPES = Map.of(
      STATUS_FIELD, PlayerGearItemStatus.class,
      RARITY_FIELD, Rarity.class,
      TYPE_FIELD, GearItemType.class,
      ELEMENT_FIELD, Element.class,
      GEAR_SET_FIELD, GearItemSet.class
  );

  private static final Map<String, Function<PlayerGearItemTable, TableEx<?>>> FIELD_TABLES = Map.of(
      RARITY_FIELD, t -> t.asTableEx().gearItem(JoinType.LEFT),
      TYPE_FIELD, t -> t.asTableEx().gearItem(JoinType.LEFT),
      ELEMENT_FIELD, t -> t.asTableEx().gearItem(JoinType.LEFT),
      GEAR_SET_FIELD, t -> t.asTableEx().gearItem(JoinType.LEFT)
  );

  private final JSqlClient sql;

  public PlayerGearItemRepository(JSqlClient sql) {
    super(sql, FIELD_ENUM_TYPES);
    this.sql = sql;
  }

  public long count(long playerId) {
    var table = PLAYER_GEAR_ITEM_TABLE;
    return sql.createQuery(table)
        .where(table.inventory().playerId().eq(playerId))
        .select(table.count())
        .fetchOne();
  }

  public <V extends View<PlayerGearItem>> Page<V> searchView(
      long playerId,
      RequestQueryOptions options,
      RequestPaging paging,
      Class<V> viewType
  ) {
    var table = PLAYER_GEAR_ITEM_TABLE;
    return fetch(
        table,
        options,
        paging,
        table.fetch(viewType),
        table.inventory().playerId().eq(playerId)
    );
  }

  public PlayerGearItem save(PlayerGearItem playerGearItem, SaveMode saveMode) {
    return sql.saveCommand(playerGearItem)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public List<PlayerGearItemView> saveAll(Collection<PlayerGearItem> items, SaveMode saveMode) {
    return sql.saveEntitiesCommand(items)
        .setMode(saveMode)
        .execute()
        .toView(PlayerGearItemView::new)
        .getViewItems()
        .stream()
        .map(ViewItem::getModifiedView)
        .toList();
  }

  @Override
  protected Function<PlayerGearItemTable, TableEx<?>> defineTable(String field) {
    return FIELD_TABLES.getOrDefault(field, super.defineTable(field));
  }
}
