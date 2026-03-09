package com.soloist.player.model.repository.gear;

import static com.soloist.player.model.entity.Tables.GEAR_ITEM_TABLE;

import com.soloist.player.model.entity.gear.GearItem;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GearItemRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<GearItem>> V findView(UUID id, Class<V> viewType) {
    var table = GEAR_ITEM_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }

  public <V extends View<GearItem>> Page<V> find(int pageIndex, int pageSize, Class<V> viewType) {
    var table = GEAR_ITEM_TABLE;
    return sql.createQuery(table)
        .select(table.fetch(viewType))
        .fetchPage(pageIndex, pageSize);
  }

  public GearItem save(GearItem gearItem, SaveMode saveMode) {
    return sql.saveCommand(gearItem)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
