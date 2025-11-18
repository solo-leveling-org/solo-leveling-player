package com.sleepkqq.sololeveling.player.model.repository.task;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.TASK_TABLE;

import com.sleepkqq.sololeveling.jimmer.sql.SqlFileLoader;
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask;
import com.sleepkqq.sololeveling.player.model.entity.player.TaskTopicItem;
import com.sleepkqq.sololeveling.player.model.entity.task.Task;
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

  private static final String FIND_MATCHING_TASKS_SQL_FILE = "sql/find-matching-tasks.sql";

  private final JSqlClient sql;
  private final JdbcTemplate jdbcTemplate;
  private final SqlFileLoader sqlFileLoader;

  @Nullable
  public Task findNullable(UUID id, Fetcher<Task> fetcher) {
    var table = TASK_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table.fetch(fetcher))
        .fetchFirstOrNull();
  }

  public Task save(Task task, SaveMode saveMode) {
    return sql.saveCommand(task)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public void saveEntities(Collection<Task> tasks, SaveMode saveMode) {
    sql.saveEntitiesCommand(tasks)
        .setMode(saveMode)
        .execute();
  }

  public UUID findMatchingTasks(long playerId, Task task) {
    var topicOrdinalsArray = StreamEx.of(task.topics())
        .map(TaskTopicItem::topic)
        .map(TaskTopic::ordinal)
        .toArray(Integer.class);

    var table = TASK_TABLE;

    return sql.createQuery(table)
        .where(
            table.rarity().eq(task.rarity()),
            table.version().ne(0),
            Predicate.sql(
                "NOT EXISTS (SELECT 1 FROM player_tasks pt WHERE pt.task_id = %e AND pt.player_id = %v)",
                ctx -> {
                  ctx.expression(table.id());
                  ctx.value(playerId);
                }
            ),
            Predicate.sql(
                """
                    EXISTS (
                        SELECT 1
                        FROM task_topic_items tt
                        WHERE tt.task_id = %e
                        GROUP BY tt.task_id
                        HAVING count(DISTINCT tt.topic) = array_length(%v, 1)
                           AND count(DISTINCT CASE WHEN tt.topic = ANY(%v) THEN tt.topic END) = array_length(%v, 1)
                    )
                    """,
                ctx -> {
                  ctx.expression(table.id());
                  ctx.value(topicOrdinalsArray);
                  ctx.value(topicOrdinalsArray);
                  ctx.value(topicOrdinalsArray);
                }
            )
        )
        .select(table.id())
        .fetchFirstOrNull();
  }


  public Map<UUID, UUID> findMatchingTasks(long playerId, Collection<PlayerTask> playerTasks) {

    var inputJson = StreamEx.of(playerTasks)
        .map(p -> new JsonObject()
            .put("player_task_id", p.id())
            .put("rarity", p.task().rarity().ordinal())
            .put("topics", StreamEx.of(p.task().topics())
                .map(TaskTopicItem::topic)
                .map(TaskTopic::ordinal)
                .toList()
            )
        )
        .toListAndThen(JsonArray::new)
        .toString();

    var sqlFile = sqlFileLoader.load(FIND_MATCHING_TASKS_SQL_FILE);
    return StreamEx.of(jdbcTemplate.queryForList(sqlFile, inputJson, playerId))
        .toMap(
            row -> (UUID) row.get("player_task_id"),
            row -> (UUID) row.get("task_id")
        );
  }
}
