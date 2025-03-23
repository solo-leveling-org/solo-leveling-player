package com.sleepkqq.sololeveling.player.service.repository.task;

import com.sleepkqq.sololeveling.player.service.model.task.Task;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {

}
