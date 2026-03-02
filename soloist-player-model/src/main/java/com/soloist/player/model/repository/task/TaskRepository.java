package com.soloist.player.model.repository.task;

import static com.soloist.player.model.entity.Tables.TASK_TABLE;
import static com.soloist.player.model.entity.Tables.VECTOR_TASK_TABLE;
import static com.soloist.player.model.entity.task.Task.RARITY_FIELD;
import static com.soloist.player.model.entity.task.Task.TOPICS_FIELD;

import com.soloist.jimmer.sql.SqlFileLoader;
import com.soloist.player.model.entity.task.PlayerTask;
import com.soloist.player.model.entity.player.TaskTopicItem;
import com.soloist.player.model.entity.task.Task;
import com.soloist.player.model.entity.task.dto.VectorizeTaskView;
import com.soloist.player.model.entity.task.enums.TaskTopic;
import com.soloist.proto.common.RequestPaging;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

  private static final String FIND_MATCHING_TASKS_SQL_FILE = "sql/find-matching-tasks.sql";

  private final JSqlClient sql;
  private final JdbcTemplate jdbcTemplate;
  private final SqlFileLoader sqlFileLoader;

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

  public UUID findMatchingTask(long playerId, Task task) {
    var topicOrdinalsArray = StreamEx.of(task.topics())
        .map(TaskTopicItem::topic)
        .map(TaskTopic::ordinal)
        .toArray(Integer.class);

    var table = TASK_TABLE;
    return sql.createQuery(table)
        .where(
            table.rarity().eq(task.rarity()),
            table.version().ne(0),
            table.deprecated().eq(false),
            Predicate.sql(
                "NOT EXISTS (SELECT 1 FROM player.player_tasks pt WHERE pt.task_id = %e AND pt.player_id = %v)",
                ctx -> {
                  ctx.expression(table.id());
                  ctx.value(playerId);
                }
            ),
            Predicate.sql(
                """
                    EXISTS (
                        SELECT 1
                        FROM player.task_topic_items tt
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
            .put(RARITY_FIELD, p.task().rarity().ordinal())
            .put(TOPICS_FIELD, StreamEx.of(p.task().topics())
                .map(TaskTopicItem::topic)
                .map(TaskTopic::ordinal)
                .sorted()
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

  public int deprecateAll() {
    var table = TASK_TABLE;
    return sql.createUpdate(table)
        .where(table.deprecated().eq(false))
        .set(table.deprecated(), true)
        .execute();
  }

  public int deprecateByTopic(TaskTopic topic) {
    var table = TASK_TABLE;
    return sql.createUpdate(table)
        .where(table.deprecated().eq(false))
        .where(table.asTableEx().topics().topic().eq(topic))
        .set(table.deprecated(), true)
        .execute();
  }

  public Page<VectorizeTaskView> findToVectorize(RequestPaging paging) {
    var t = TASK_TABLE;
    var vt = VECTOR_TASK_TABLE;

    var nonVector = sql.createSubQuery(vt)
        .where(vt.id().eq(t.id()))
        .notExists();

    return sql.createQuery(t)
        .where(
            nonVector,
            t.deprecated().eq(false),
            t.version().gt(0)
        )
        .orderBy(t.createdAt().asc())
        .select(t.fetch(VectorizeTaskView.class))
        .fetchPage(paging.getPage(), paging.getPageSize());
  }
}
