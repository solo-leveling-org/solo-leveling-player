package com.sleepkqq.sololeveling.player.model.repository.task;

import com.sleepkqq.sololeveling.player.model.entity.task.Task;
import java.util.UUID;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JRepository<Task, UUID> {

}
