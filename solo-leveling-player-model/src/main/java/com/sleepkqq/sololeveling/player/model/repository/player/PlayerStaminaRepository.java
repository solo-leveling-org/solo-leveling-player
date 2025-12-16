package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_STAMINA_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStamina;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStaminaFetcher;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerStaminaRepository {

  private final JSqlClient sql;

  @Nullable
  public PlayerStamina find(long playerId, PlayerStaminaFetcher fetcher) {
    var table = PLAYER_STAMINA_TABLE;
    return sql.createQuery(table)
        .where(table.playerId().eq(playerId))
        .select(table.fetch(fetcher))
        .fetchFirstOrNull();
  }

  public PlayerStamina save(PlayerStamina stamina, SaveMode saveMode) {
    return sql.saveCommand(stamina)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
