package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.PLAYER_GEAR_ITEM_TABLE;

import com.soloist.player.model.entity.player.PlayerGearItem;
import com.soloist.player.model.entity.player.dto.PlayerGearItemView;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.BatchSaveResult.View.ViewItem;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerGearItemRepository {

  private final JSqlClient sql;

  public <V extends View<PlayerGearItem>> Page<V> findByPlayerIdView(
      long playerId,
      int pageIndex,
      int pageSize,
      Class<V> viewType
  ) {
    var table = PLAYER_GEAR_ITEM_TABLE;
    return sql.createQuery(table)
        .where(table.playerId().eq(playerId))
        .select(table.fetch(viewType))
        .fetchPage(pageIndex, pageSize);
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
}
