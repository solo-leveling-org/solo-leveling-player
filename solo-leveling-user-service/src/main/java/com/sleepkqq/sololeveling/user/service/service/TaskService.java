package com.sleepkqq.sololeveling.user.service.service;

import com.sleepkqq.sololeveling.user.service.model.Task;
import com.sleepkqq.sololeveling.user.service.repository.TaskRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  public Task get(UUID id) {
    return find(id).orElseThrow(() -> new IllegalArgumentException("Task not found taskId=" + id));
  }

  public Optional<Task> find(UUID id) {
    return taskRepository.findById(id);
  }

  public Task save(Task task) {
    return taskRepository.save(task);
  }
}
