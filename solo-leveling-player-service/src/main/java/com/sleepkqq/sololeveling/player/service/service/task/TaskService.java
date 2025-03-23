package com.sleepkqq.sololeveling.player.service.service.task;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.task.Task;
import com.sleepkqq.sololeveling.player.service.repository.task.TaskRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  public Task get(UUID id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(Task.class, id));
  }

  public Optional<Task> find(UUID id) {
    return taskRepository.findById(id);
  }

  public Task save(Task task) {
    return taskRepository.save(task);
  }
}
