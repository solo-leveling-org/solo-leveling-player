package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_BALANCE_TRANSACTION_TABLE;
import static com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction.CAUSE_FIELD;
import static com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction.TYPE_FIELD;

import com.sleepkqq.sololeveling.jimmer.fetcher.PageFetcher;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType;
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions;
import java.util.Map;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerBalanceTransactionRepository extends PageFetcher {

  private final JSqlClient sql;

  public PlayerBalanceTransactionRepository(JSqlClient sql) {
    super(sql, Map.of(
        CAUSE_FIELD, PlayerBalanceTransactionCause.class,
        TYPE_FIELD, PlayerBalanceTransactionType.class
    ));
    this.sql = sql;
  }

  public PlayerBalanceTransaction save(PlayerBalanceTransaction transaction, SaveMode saveMode) {
    return sql.saveCommand(transaction)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public <V extends View<PlayerBalanceTransaction>> Page<V> searchView(
      long playerId,
      RequestQueryOptions options,
      Class<V> viewType
  ) {
    var table = PLAYER_BALANCE_TRANSACTION_TABLE;
    return fetch(
        table,
        options,
        table.fetch(viewType),
        table.balance().playerId().eq(playerId)
    );
  }
}
