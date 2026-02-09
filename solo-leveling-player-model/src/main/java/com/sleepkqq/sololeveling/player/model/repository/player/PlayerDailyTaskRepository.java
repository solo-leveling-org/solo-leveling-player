package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_DAILY_TASK_TABLE;
import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerDailyTaskRepository {

  private final JSqlClient sql;

  public void saveEntities(Collection<PlayerDailyTask> playerTasks, SaveMode saveMode) {
    sql.saveEntitiesCommand(playerTasks)
        .setMode(saveMode)
        .execute();
  }

  public List<Long> findPlayersToInit() {
    var p = PLAYER_TABLE;
    var pdt = PLAYER_DAILY_TASK_TABLE;

    return sql.createQuery(p)
        .where(
            sql.createSubQuery(pdt)
                .where(pdt.playerId().eq(p.id()))
                .notExists()
        )
        .select(p.id())
        .execute();
  }

  public <V extends View<PlayerDailyTask>> List<V> findView(Class<V> viewType) {
    var pdt = PLAYER_DAILY_TASK_TABLE;
    return sql.createQuery(pdt)
        .select(pdt.fetch(viewType))
        .execute();
  }
}
