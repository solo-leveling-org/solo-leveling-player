package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.STAMINA_TABLE;

import com.soloist.player.model.entity.player.Stamina;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StaminaRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<Stamina>> V findView(long playerId, Class<V> viewType) {
    var p = STAMINA_TABLE;
    return sql.createQuery(p)
        .where(p.playerId().eq(playerId))
        .select(p.fetch(viewType))
        .fetchFirstOrNull();
  }

  public Stamina save(Stamina stamina, SaveMode saveMode) {
    return sql.saveCommand(stamina)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
