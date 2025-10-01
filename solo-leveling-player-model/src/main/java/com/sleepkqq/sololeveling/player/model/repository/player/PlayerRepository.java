package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.Player;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerFetcher;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerRepository {

  private final JSqlClient sql;

  @Nullable
  public Player findNullable(long id, PlayerFetcher fetcher) {
    var table = PLAYER_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(fetcher))
        .fetchFirstOrNull();
  }

  @Nullable
  public <V extends View<Player>> V findView(long id, Class<V> viewType) {
    var table = PLAYER_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }

  public Player save(Player player, SaveMode saveMode) {
    return sql.saveCommand(player)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
