package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TASK_TOPIC_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerTaskTopicRepository {

  private final JSqlClient sql;

  public <V extends View<PlayerTaskTopic>> List<V> findViewByPlayerId(
      long playerId,
      Class<V> viewType
  ) {
    var table = PLAYER_TASK_TOPIC_TABLE;
    return sql.createQuery(table)
        .where(table.playerId().eq(playerId))
        .select(table.fetch(viewType))
        .execute();
  }

  public void saveEntities(Collection<PlayerTaskTopic> entities, SaveMode saveMode) {
    sql.saveEntitiesCommand(entities)
        .setMode(saveMode)
        .execute();
  }

  public PlayerTaskTopic save(PlayerTaskTopic playerTaskTopic, SaveMode saveMode) {
    return sql.saveCommand(playerTaskTopic)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
