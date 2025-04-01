package com.sleepkqq.sololeveling.player.service.repository.player;

import static com.sleepkqq.sololeveling.player.service.model.Fetchers.PLAYER_TASK_FETCHER;
import static com.sleepkqq.sololeveling.player.service.model.Fetchers.TASK_FETCHER;
import static com.sleepkqq.sololeveling.player.service.model.Tables.PLAYER_TASK_TABLE;

import com.sleepkqq.sololeveling.player.service.model.player.PlayerTask;
import com.sleepkqq.sololeveling.player.service.model.player.enums.PlayerTaskStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerTaskRepository extends JRepository<PlayerTask, UUID> {

  @Transactional
  default List<PlayerTask> findByPlayerIdAndStatusIn(
      long playerId,
      Collection<PlayerTaskStatus> statuses
  ) {
    var table = PLAYER_TASK_TABLE;
    return sql().createQuery(table)
        .where(
            table.playerId().eq(playerId),
            table.status().in(statuses)
        )
        .select(table.fetch(PLAYER_TASK_FETCHER
            .allScalarFields()
            .task(TASK_FETCHER.allScalarFields())
        ))
        .map(Function.identity());
  }

  @Transactional
  default long countByPlayerIdAndStatusIn(
      long playerId,
      Collection<PlayerTaskStatus> statuses
  ) {
    var table = PLAYER_TASK_TABLE;
    return sql()
        .createQuery(table)
        .where(
            table.playerId().eq(playerId),
            table.status().in(statuses)
        )
        .select(table.count())
        .fetchOne();
  }

  @Transactional
  default Optional<UUID> findIdByPlayerIdAndTaskId(long playerId, UUID taskId) {
    var table = PLAYER_TASK_TABLE;
    return sql().createQuery(table)
        .where(
            table.playerId().eq(playerId),
            table.taskId().eq(taskId)
        )
        .select(table.id())
        .fetchOptional();
  }

  @Transactional
  default void setStatus(UUID id, PlayerTaskStatus status, LocalDateTime now) {
    var table = PLAYER_TASK_TABLE;
    sql().createUpdate(table)
        .where(table.id().eq(id))
        .set(table.status(), status)
        .set(table.version(), table.version().plus(1))
        .set(table.updatedAt(), now)
        .execute();
  }
}