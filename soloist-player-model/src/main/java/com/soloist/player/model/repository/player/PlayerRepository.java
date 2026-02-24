package com.soloist.player.model.repository.player;

import static com.soloist.player.model.entity.Tables.PLAYER_TABLE;

import com.soloist.player.model.entity.player.Player;
import com.soloist.player.model.entity.player.PlayerFetcher;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerRepository {

  private final JSqlClient sql;

  @Nullable
  public Player findNullable(long id, PlayerFetcher fetcher) {
    var p = PLAYER_TABLE;
    return sql.createQuery(p)
        .where(p.id().eq(id))
        .select(p.fetch(fetcher))
        .fetchFirstOrNull();
  }

  @Nullable
  public <V extends View<Player>> V findView(long id, Class<V> viewType) {
    var p = PLAYER_TABLE;
    return sql.createQuery(p)
        .where(p.id().eq(id))
        .select(p.fetch(viewType))
        .fetchFirstOrNull();
  }

  public Player save(Player player, SaveMode saveMode) {
    return save(player, saveMode, null);
  }

  public Player save(Player player, SaveMode saveMode, AssociatedSaveMode associatedSaveMode) {
    var command = sql.saveCommand(player)
        .setMode(saveMode);

    if (associatedSaveMode != null) {
      command = command.setAssociatedModeAll(associatedSaveMode);
    }

    return command
        .execute()
        .getModifiedEntity();
  }
}
