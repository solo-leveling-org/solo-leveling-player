package com.sleepkqq.sololeveling.player.model.repository.player;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.PLAYER_TASK_TABLE;
import static com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask.STATUS_FIELD;
import static com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem.TOPIC_FIELD;
import static com.sleepkqq.sololeveling.player.model.entity.task.Task.RARITY_FIELD;

import com.sleepkqq.sololeveling.jimmer.enums.LocalizableEnum;
import com.sleepkqq.sololeveling.jimmer.fetcher.PageFetcher;
import com.sleepkqq.sololeveling.player.model.entity.Fetchers;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskFetcher;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTable;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity;
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic;
import com.sleepkqq.sololeveling.proto.player.RequestPaging;
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions;
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
import org.babyfish.jimmer.sql.fetcher.Fetcher;
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
  public PlayerTask find(UUID id, PlayerTaskFetcher fetcher) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(fetcher))
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

  public List<PlayerTask> findByPlayerIdAndStatusIn(
      long playerId,
      Collection<PlayerTaskStatus> statuses,
      Fetcher<PlayerTask> fetcher
  ) {
    var table = PLAYER_TASK_TABLE;
    return sql.createQuery(table)
        .where(
            table.playerId().eq(playerId),
            table.status().in(statuses)
        )
        .select(table.fetch(fetcher))
        .execute();
  }

  public List<PlayerTask> findPreparingTasksForRetry() {
    var table = PLAYER_TASK_TABLE;
    var oneMinuteAgo = Instant.now().minus(1, ChronoUnit.MINUTES);

    return sql.createQuery(table)
        .where(
            table.status().eq(PlayerTaskStatus.PREPARING),
            table.updatedAt().le(oneMinuteAgo)
        )
        .select(table.fetch(Fetchers.PLAYER_TASK_FETCHER.allScalarFields()
            .player()
            .task(Fetchers.TASK_FETCHER
                .version()
                .topics(Fetchers.TASK_TOPIC_ITEM_FETCHER.topic())
            )
        ))
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
