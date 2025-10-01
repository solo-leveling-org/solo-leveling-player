package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TASK_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerTaskRepository {

  private final JSqlClient sql;

  public PlayerTask save(PlayerTask playerTask, SaveMode saveMode) {
    return sql.saveCommand(playerTask)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public void saveEntities(Collection<PlayerTask> playerTasks, SaveMode saveMode) {
    sql.saveEntitiesCommand(playerTasks)
        .setMode(saveMode)
        .execute();
  }

  public <V extends View<PlayerTask>> List<V> findByPlayerIdAndStatusIn(
      long playerId,
      Collection<PlayerTaskStatus> statuses,
      Class<V> viewType
  ) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(Predicate.and(
            table.playerId().eq(playerId),
            table.status().in(statuses)
        ))
        .select(table.fetch(viewType))
        .execute();
  }

  public long countByPlayerIdAndStatusIn(long playerId, Collection<PlayerTaskStatus> statuses) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(Predicate.and(
            table.playerId().eq(playerId),
            table.status().in(statuses)
        ))
        .selectCount()
        .fetchFirst();
  }

  public List<PlayerTask> findByPlayerIdAndTaskIdIn(long playerId, Collection<UUID> taskIds) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(Predicate.and(
            table.playerId().eq(playerId),
            table.taskId().in(taskIds)
        ))
        .select(table)
        .execute();
  }

  public <V extends View<PlayerTask>> List<V> findByStatus(
      PlayerTaskStatus status,
      Class<V> viewType
  ) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(table.status().eq(status))
        .select(table.fetch(viewType))
        .execute();
  }
}
