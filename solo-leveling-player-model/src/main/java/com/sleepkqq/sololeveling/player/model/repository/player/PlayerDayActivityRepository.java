package com.sleepkqq.sololeveling.player.model.repository.player;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDayActivity;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerDayActivityRepository {

  private final JSqlClient sql;

  public PlayerDayActivity save(PlayerDayActivity activity, SaveMode saveMode) {
    return sql.saveCommand(activity)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
