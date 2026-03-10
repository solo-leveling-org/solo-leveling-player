package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.PLAYER_CONSUMABLE_TABLE;

import com.soloist.player.model.entity.gear.enums.ConsumableType;
import com.soloist.player.model.entity.player.PlayerConsumable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerConsumableRepository {

  private final JSqlClient sql;

  @Nullable
  public PlayerConsumable find(long playerId, ConsumableType type) {
    var table = PLAYER_CONSUMABLE_TABLE;
    return sql.createQuery(table)
        .where(table.inventory().playerId().eq(playerId))
        .where(table.consumableType().eq(type))
        .select(table)
        .fetchFirstOrNull();
  }

  public long sumQuantity(long playerId) {
    var table = PLAYER_CONSUMABLE_TABLE;
    var result = sql.createQuery(table)
        .where(table.inventory().playerId().eq(playerId))
        .select(table.quantity().sum())
        .fetchOneOrNull();
    return result != null ? result : 0L;
  }

  public List<PlayerConsumable> find(long playerId) {
    var table = PLAYER_CONSUMABLE_TABLE;
    return sql.createQuery(table)
        .where(table.inventory().playerId().eq(playerId))
        .select(table)
        .execute();
  }

  public PlayerConsumable save(PlayerConsumable consumable, SaveMode saveMode) {
    return sql.saveCommand(consumable)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
