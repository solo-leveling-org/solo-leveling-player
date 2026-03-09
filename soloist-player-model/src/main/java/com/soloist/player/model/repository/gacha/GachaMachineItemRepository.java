package com.soloist.player.model.repository.gacha;

import static com.soloist.player.model.entity.Tables.GACHA_MACHINE_ITEM_TABLE;

import com.soloist.player.model.entity.gacha.GachaMachineItem;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GachaMachineItemRepository {

  private final JSqlClient sql;

  public <V extends View<GachaMachineItem>> List<V> findByMachineIdView(
      UUID gachaMachineId,
      Class<V> viewType
  ) {
    var table = GACHA_MACHINE_ITEM_TABLE;
    return sql.createQuery(table)
        .where(table.gachaMachineId().eq(gachaMachineId))
        .select(table.fetch(viewType))
        .execute();
  }

  public void saveEntities(Collection<GachaMachineItem> items, SaveMode saveMode) {
    sql.saveEntitiesCommand(items)
        .setMode(saveMode)
        .execute();
  }

  public GachaMachineItem save(GachaMachineItem item, SaveMode saveMode) {
    return sql.saveCommand(item)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public int deleteByMachineAndGearItem(UUID gachaMachineId, UUID gearItemId) {
    var table = GACHA_MACHINE_ITEM_TABLE;
    return sql.createDelete(table)
        .where(table.gachaMachineId().eq(gachaMachineId))
        .where(table.gearItemId().eq(gearItemId))
        .execute();
  }
}
