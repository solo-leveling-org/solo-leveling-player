package com.sleepkqq.sololeveling.player.model.repository.task;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.TASK_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.player.enums.Rarity;
import com.sleepkqq.sololeveling.player.model.entity.task.Task;
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

  private final JSqlClient sql;

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

  public UUID find(long playerId, Rarity rarity, Collection<TaskTopic> topics) {
    var topicOrdinalsArray = StreamEx.of(topics)
        .map(TaskTopic::ordinal)
        .toArray(Integer.class);

    var table = TASK_TABLE;

    return sql.createQuery(table)
        .where(
            table.asTableEx().playerTasks().playerId().ne(playerId),
            table.rarity().eq(rarity),
            table.version().ne(0),
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
                  ctx.expression(table.id());          // %e: заменит на tb_1_.ID (ID основной задачи)
                  ctx.value(topicOrdinalsArray);       // %v: первый array_length
                  ctx.value(topicOrdinalsArray);       // %v: ANY
                  ctx.value(topicOrdinalsArray);       // %v: второй array_length
                }
            )
        )
        .select(table.id())
        .fetchFirstOrNull();
  }
}
