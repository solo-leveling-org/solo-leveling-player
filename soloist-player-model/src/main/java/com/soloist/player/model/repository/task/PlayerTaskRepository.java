package com.soloist.player.model.repository.task;

import static com.soloist.player.model.entity.Tables.PLAYER_TASK_TABLE;
import static com.soloist.player.model.entity.task.PlayerTask.STATUS_FIELD;
import static com.soloist.player.model.entity.player.TaskTopicItem.TOPIC_FIELD;
import static com.soloist.player.model.entity.task.Task.RARITY_FIELD;

import com.soloist.jimmer.enums.LocalizableEnum;
import com.soloist.jimmer.fetcher.PageFetcher;
import com.soloist.player.model.entity.task.PlayerTask;
import com.soloist.player.model.entity.task.PlayerTaskTable;
import com.soloist.player.model.entity.task.dto.PreparingPlayerTaskView;
import com.soloist.player.model.entity.task.enums.PlayerTaskStatus;
import com.soloist.player.model.entity.player.enums.Rarity;
import com.soloist.player.model.entity.task.enums.TaskTopic;
import com.soloist.proto.common.RequestPaging;
import com.soloist.proto.common.RequestQueryOptions;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.ast.table.TableEx;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerTaskRepository extends PageFetcher<PlayerTask, PlayerTaskTable> {

  public static final Map<String, Class<? extends LocalizableEnum>> FIELD_ENUM_TYPES = Map.of(
      RARITY_FIELD, Rarity.class,
      TOPIC_FIELD, TaskTopic.class,
      STATUS_FIELD, PlayerTaskStatus.class
  );

  public static final Map<Class<? extends LocalizableEnum>, Predicate<? extends LocalizableEnum>> ENUM_TYPE_PREDICATES
      = Map.of(
      PlayerTaskStatus.class,
      s -> s != PlayerTaskStatus.PREPARING && s != PlayerTaskStatus.IN_PROGRESS
  );

  private static final Map<String, Function<PlayerTaskTable, TableEx<?>>> FIELD_TABLES = Map.of(
      RARITY_FIELD, t -> t.asTableEx().task(JoinType.LEFT),
      TOPIC_FIELD, t -> t.asTableEx().task(JoinType.LEFT).topics(JoinType.LEFT)
  );

  private final JSqlClient sql;

  public PlayerTaskRepository(JSqlClient sql) {
    super(sql, FIELD_ENUM_TYPES);
    this.sql = sql;
  }

  public <V extends View<PlayerTask>> Page<V> searchView(
      long playerId,
      RequestQueryOptions options,
      RequestPaging paging,
      Class<V> viewType
  ) {
    var table = PLAYER_TASK_TABLE;
    return fetch(table, options, paging, table.fetch(viewType), table.playerId().eq(playerId));
  }

  public void saveEntities(Collection<PlayerTask> playerTasks, SaveMode saveMode) {
    sql.saveEntitiesCommand(playerTasks)
        .setMode(saveMode)
        .execute();
  }

  @Nullable
  public <V extends View<PlayerTask>> V findView(UUID id, Class<V> viewType) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(viewType))
        .fetchFirstOrNull();
  }

  public <V extends View<PlayerTask>> List<V> findByPlayerIdAndStatusIn(
      long playerId,
      Collection<PlayerTaskStatus> statuses,
      Class<V> viewType
  ) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(
            table.playerId().eq(playerId),
            table.status().in(statuses)
        )
        .select(table.fetch(viewType))
        .execute();
  }

  public List<PreparingPlayerTaskView> findPreparingTasksForRetry() {
    var table = PLAYER_TASK_TABLE;
    var oneMinuteAgo = Instant.now().minus(1, ChronoUnit.MINUTES);

    return sql.createQuery(table)
        .where(
            table.status().eq(PlayerTaskStatus.PREPARING),
            table.updatedAt().le(oneMinuteAgo)
        )
        .select(table.fetch(PreparingPlayerTaskView.class))
        .execute();
  }

  public List<PlayerTask> findByPlayerIdAndTaskIdIn(long playerId, Collection<UUID> taskIds) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(
            table.playerId().eq(playerId),
            table.taskId().in(taskIds)
        )
        .select(table)
        .execute();
  }

  @Override
  protected Function<PlayerTaskTable, TableEx<?>> defineTable(String field) {
    return FIELD_TABLES.getOrDefault(field, super.defineTable(field));
  }
}
