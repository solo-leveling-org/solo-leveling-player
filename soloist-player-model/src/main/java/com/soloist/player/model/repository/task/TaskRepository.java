package com.soloist.player.model.repository.task;

import static com.soloist.player.model.entity.Tables.TASK_TABLE;

import com.soloist.player.model.entity.task.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

  private final JSqlClient sql;

  public List<Task> findByPlayerIdAndDay(long playerId, LocalDate day) {
    var t = TASK_TABLE;
    return sql.createQuery(t)
        .where(t.player().id().eq(playerId))
        .where(t.day().eq(day))
        .select(t)
        .execute();
  }

  public List<Task> findCompletedHistory(long playerId, LocalDate excludeDay, int page, int pageSize) {
    var t = TASK_TABLE;
    return sql.createQuery(t)
        .where(t.player().id().eq(playerId))
        .where(t.completed().eq(true))
        .where(t.day().ne(excludeDay))
        .orderBy(t.day().desc(), t.createdAt().desc())
        .select(t)
        .limit(pageSize, (long) page * pageSize)
        .execute();
  }

  public long countCompletedHistory(long playerId, LocalDate excludeDay) {
    var t = TASK_TABLE;
    return sql.createQuery(t)
        .where(t.player().id().eq(playerId))
        .where(t.completed().eq(true))
        .where(t.day().ne(excludeDay))
        .select(t.count())
        .fetchOne();
  }

  public Task save(Task task, SaveMode saveMode) {
    return sql.saveCommand(task)
        .setMode(saveMode)
        .execute()
        .getModifiedEntity();
  }

  public void saveAll(List<Task> tasks, SaveMode saveMode) {
    sql.saveEntitiesCommand(tasks)
        .setMode(saveMode)
        .execute();
  }

  public Optional<Task> findById(UUID id) {
    return Optional.ofNullable(sql.findById(Task.class, id));
  }

  public List<Long> findDistinctPlayerIdsByDay(LocalDate day) {
    var t = TASK_TABLE;
    return sql.createQuery(t)
        .where(t.day().eq(day))
        .groupBy(t.player().id())
        .select(t.player().id())
        .execute();
  }
}
