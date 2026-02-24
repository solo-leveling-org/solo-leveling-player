package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.PLAYER_BALANCE_TRANSACTION_TABLE;
import static com.soloist.player.model.entity.player.PlayerBalanceTransaction.CAUSE_FIELD;
import static com.soloist.player.model.entity.player.PlayerBalanceTransaction.TYPE_FIELD;

import com.soloist.jimmer.enums.LocalizableEnum;
import com.soloist.jimmer.fetcher.PageFetcher;
import com.soloist.player.model.entity.player.PlayerBalanceTransaction;
import com.soloist.player.model.entity.player.PlayerBalanceTransactionTable;
import com.soloist.player.model.entity.player.enums.PlayerBalanceTransactionCause;
import com.soloist.player.model.entity.player.enums.PlayerBalanceTransactionType;
import com.soloist.proto.player.RequestPaging;
import com.soloist.proto.player.RequestQueryOptions;
import java.util.Map;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerBalanceTransactionRepository extends
    PageFetcher<PlayerBalanceTransaction, PlayerBalanceTransactionTable> {

  public static final Map<String, Class<? extends LocalizableEnum>> FIELD_ENUM_TYPES = Map.of(
      CAUSE_FIELD, PlayerBalanceTransactionCause.class,
      TYPE_FIELD, PlayerBalanceTransactionType.class
  );

  private final JSqlClient sql;

  public PlayerBalanceTransactionRepository(JSqlClient sql) {
    super(sql, FIELD_ENUM_TYPES);
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
      RequestPaging paging,
      Class<V> viewType
  ) {
    var table = PLAYER_BALANCE_TRANSACTION_TABLE;
    return fetch(
        table,
        options,
        paging,
        table.fetch(viewType),
        table.balance().playerId().eq(playerId)
    );
  }
}
