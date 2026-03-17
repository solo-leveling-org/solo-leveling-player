package com.soloist.player.model.repository.balance;

import static com.soloist.player.model.entity.Tables.BALANCE_TABLE;

import com.soloist.player.model.entity.balance.Balance;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BalanceRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<Balance>> V findView(long playerId, Class<V> viewType) {
    var table = BALANCE_TABLE;
    return sql.createQuery(table)
        .where(table.playerId().eq(playerId))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }

  public Balance save(Balance balance, SaveMode saveMode) {
    return sql.saveCommand(balance)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
