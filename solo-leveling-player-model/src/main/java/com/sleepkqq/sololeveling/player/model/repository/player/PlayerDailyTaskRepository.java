package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_DAILY_TASK_TABLE;
import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.DailyTaskType;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
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

  public List<Long> findPlayersToInit(DailyTaskType type) {
    var p = PLAYER_TABLE;
    var pdt = PLAYER_DAILY_TASK_TABLE;

    return sql.createQuery(p)
        .where(
            sql.createSubQuery(pdt)
                .where(pdt.playerId().eq(p.id()))
                .where(pdt.type().eq(type))
                .notExists()
        )
        .select(p.id())
        .distinct()
        .execute();
  }

  public <V extends View<PlayerDailyTask>> List<V> findView(Class<V> viewType) {
    var pdt = PLAYER_DAILY_TASK_TABLE;
    return sql.createQuery(pdt)
        .select(pdt.fetch(viewType))
        .execute();
  }

  @Nullable
  public PlayerDailyTask findNullable(long playerId, DailyTaskType type) {
    var pdt = PLAYER_DAILY_TASK_TABLE;
    return sql.createQuery(pdt)
        .where(
            pdt.playerId().eq(playerId),
            pdt.type().eq(type)
        )
        .select(pdt)
        .fetchFirstOrNull();
  }

  public PlayerDailyTask save(PlayerDailyTask task, SaveMode saveMode) {
    return sql.saveCommand(task)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }
}
