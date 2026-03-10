package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.INVENTORY_TABLE;

import com.soloist.player.model.entity.player.Inventory;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InventoryRepository {

  private final JSqlClient sql;

  public <V extends View<Inventory>> V findView(long playerId, Class<V> viewType) {
    var table = INVENTORY_TABLE;
    return sql.createQuery(table)
        .where(table.playerId().eq(playerId))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }
}
