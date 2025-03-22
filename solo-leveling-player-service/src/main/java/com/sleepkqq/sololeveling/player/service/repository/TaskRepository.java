package com.sleepkqq.sololeveling.player.service.repository;

import com.sleepkqq.sololeveling.player.service.model.Task;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {

}
