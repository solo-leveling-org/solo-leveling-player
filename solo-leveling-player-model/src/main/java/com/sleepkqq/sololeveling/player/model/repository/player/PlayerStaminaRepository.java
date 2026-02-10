package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_STAMINA_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStamina;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerStaminaRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<PlayerStamina>> V findView(long playerId, Class<V> viewType) {
    var p = PLAYER_STAMINA_TABLE;
    return sql.createQuery(p)
        .where(p.playerId().eq(playerId))
        .select(p.fetch(viewType))
        .fetchFirstOrNull();
  }

  public PlayerStamina save(PlayerStamina stamina, SaveMode saveMode) {
    return sql.saveCommand(stamina)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
