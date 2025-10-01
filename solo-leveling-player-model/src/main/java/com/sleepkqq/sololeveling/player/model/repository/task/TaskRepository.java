package com.sleepkqq.sololeveling.player.model.repository.task;

import static com.sleepkqq.sololeveling.player.model.entity.Tables.TASK_TABLE;

import com.sleepkqq.sololeveling.player.model.entity.task.Task;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

  private final JSqlClient sql;

  @Nullable
  public Task findNullable(UUID id) {
    var table = TASK_TABLE;
    return sql.createQuery(table)
        .where(table.id().eq(id))
        .select(table)
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
}
