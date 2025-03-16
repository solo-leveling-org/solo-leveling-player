package com.sleepkqq.sololeveling.user.service.repository;

import com.sleepkqq.sololeveling.user.service.model.UserTasks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTasksRepository extends JpaRepository<UserTasks, Long> {

}
