package com.sleepkqq.sololeveling.player.service.service.task;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.Immutables;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import com.sleepkqq.sololeveling.player.service.repository.task.TaskRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  @Transactional
  public Task get(UUID id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(Task.class, id));
  }

  @Transactional
  public Optional<Task> find(UUID id) {
    return taskRepository.findById(id);
  }

  @Transactional
  public int getVersion(UUID id) {
    return taskRepository.findVersionById(id)
        .orElseThrow(() -> new ModelNotFoundException(Task.class, id));
  }

  @Transactional
  public Task create(Task task) {
    return taskRepository.save(task, SaveMode.INSERT_ONLY).getModifiedEntity();
  }

  @Transactional
  public Task update(Task task, LocalDateTime now) {
    return taskRepository.update(Immutables.createTask(task, t ->
        t.setUpdatedAt(now)
    ));
  }

  @Transactional
  public Task update(Task task) {
    return update(task, LocalDateTime.now());
  }
}
