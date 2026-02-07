package com.sleepkqq.sololeveling.player.model.repository.player;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerStamina;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerStaminaRepository {

  private final JSqlClient sql;

  public PlayerStamina save(PlayerStamina stamina, SaveMode saveMode) {
    return sql.saveCommand(stamina)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
