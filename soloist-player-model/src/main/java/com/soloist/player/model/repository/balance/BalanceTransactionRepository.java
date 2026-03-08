package com.soloist.player.model.repository.balance;

import static com.soloist.player.model.entity.Tables.BALANCE_TRANSACTION_TABLE;
import static com.soloist.player.model.entity.balance.BalanceTransaction.CAUSE_FIELD;
import static com.soloist.player.model.entity.balance.BalanceTransaction.TYPE_FIELD;

import com.soloist.jimmer.enums.LocalizableEnum;
import com.soloist.jimmer.fetcher.PageFetcher;
import com.soloist.player.model.entity.balance.BalanceTransaction;
import com.soloist.player.model.entity.balance.BalanceTransactionTable;
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause;
import com.soloist.player.model.entity.balance.enums.BalanceTransactionType;
import com.soloist.proto.common.RequestPaging;
import com.soloist.proto.common.RequestQueryOptions;
import java.util.Map;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
public class BalanceTransactionRepository extends
    PageFetcher<BalanceTransaction, BalanceTransactionTable> {

  public static final Map<String, Class<? extends LocalizableEnum>> FIELD_ENUM_TYPES = Map.of(
      CAUSE_FIELD, BalanceTransactionCause.class,
      TYPE_FIELD, BalanceTransactionType.class
  );

  private final JSqlClient sql;

  public BalanceTransactionRepository(JSqlClient sql) {
    super(sql, FIELD_ENUM_TYPES);
    this.sql = sql;
  }

  public BalanceTransaction save(BalanceTransaction transaction, SaveMode saveMode) {
    return sql.saveCommand(transaction)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public <V extends View<BalanceTransaction>> Page<V> searchView(
      long playerId,
      RequestQueryOptions options,
      RequestPaging paging,
      Class<V> viewType
  ) {
    var table = BALANCE_TRANSACTION_TABLE;
    return fetch(
        table,
        options,
        paging,
        table.fetch(viewType),
        table.balance().playerId().eq(playerId)
    );
  }
}
