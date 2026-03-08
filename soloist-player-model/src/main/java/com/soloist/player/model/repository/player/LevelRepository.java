package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.LEVEL_TABLE;

import com.soloist.player.model.entity.player.Level;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LevelRepository {

  private final JSqlClient sql;

  @Nullable
  public <V extends View<Level>> V findView(long playerId, Class<V> viewType) {
    var p = LEVEL_TABLE;
    return sql.createQuery(p)
        .where(p.playerId().eq(playerId))
        .select(p.fetch(viewType))
        .fetchFirstOrNull();
  }
}
