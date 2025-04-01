package com.sleepkqq.sololeveling.player.service.repository.task;

import static com.sleepkqq.sololeveling.player.service.model.Tables.TASK_TABLE;

import com.sleepkqq.sololeveling.player.service.model.task.Task;
import java.util.Optional;
import java.util.UUID;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TaskRepository extends JRepository<Task, UUID> {

  @Transactional
  default Optional<Integer> findVersionById(UUID id) {
    var table = TASK_TABLE;
    return sql().createQuery(table)
        .where(table.id().eq(id))
        .select(table.version())
        .fetchOptional();
  }
}
