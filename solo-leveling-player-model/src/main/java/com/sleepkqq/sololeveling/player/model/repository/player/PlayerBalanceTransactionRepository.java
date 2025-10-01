package com.sleepkqq.sololeveling.player.model.repository.player;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerBalanceTransaction;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerBalanceTransactionRepository {

  private final JSqlClient sql;

  public PlayerBalanceTransaction save(PlayerBalanceTransaction transaction, SaveMode saveMode) {
    return sql.saveCommand(transaction)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
