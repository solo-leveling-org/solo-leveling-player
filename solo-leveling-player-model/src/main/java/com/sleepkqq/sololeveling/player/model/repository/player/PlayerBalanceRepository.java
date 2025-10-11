package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_BALANCE_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalance;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerBalanceRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<PlayerBalance>> V findView(long playerId, Class<V> viewType) {
    var table = PLAYER_BALANCE_TABLE;
    return sql.createQuery(table)
        .where(table.playerId().eq(playerId))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }
}
