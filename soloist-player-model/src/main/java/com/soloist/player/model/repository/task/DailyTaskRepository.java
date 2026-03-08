package com.soloist.player.model.repository.task;

import static com.soloist.player.model.entity.Tables.DAILY_TASK_TABLE;
import static com.soloist.player.model.entity.Tables.PLAYER_TABLE;

import com.soloist.player.model.entity.task.DailyTask;
import com.soloist.player.model.entity.task.enums.DailyTaskType;
import com.soloist.player.model.entity.player.sealed.DailyTaskSpec;
import java.math.BigDecimal;
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
public class DailyTaskRepository {

  private final JSqlClient sql;

  public void saveEntities(Collection<DailyTask> playerTasks, SaveMode saveMode) {
    sql.saveEntitiesCommand(playerTasks)
        .setMode(saveMode)
        .execute();
  }

  public List<Long> findPlayersToInit(DailyTaskType type) {
    var p = PLAYER_TABLE;
    var pdt = DAILY_TASK_TABLE;

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

  public <V extends View<DailyTask>> List<V> findView(long playerId, Class<V> viewType) {
    var pdt = DAILY_TASK_TABLE;
    return sql.createQuery(pdt)
        .where(pdt.playerId().eq(playerId))
        .select(pdt.fetch(viewType))
        .execute();
  }

  @Nullable
  public DailyTask findNullable(long playerId, DailyTaskType type) {
    var pdt = DAILY_TASK_TABLE;
    return sql.createQuery(pdt)
        .where(
            pdt.playerId().eq(playerId),
            pdt.type().eq(type)
        )
        .select(pdt)
        .fetchFirstOrNull();
  }

  public DailyTask save(DailyTask task, SaveMode saveMode) {
    return sql.saveCommand(task)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public long replace(DailyTaskType type, DailyTaskSpec spec) {
    var pdt = DAILY_TASK_TABLE;
    return sql.createUpdate(pdt)
        .where(pdt.type().eq(type))
        .set(pdt.completed(), false)
        .set(pdt.progress(), BigDecimal.ZERO)
        .set(pdt.spec(), spec)
        .execute();
  }
}
