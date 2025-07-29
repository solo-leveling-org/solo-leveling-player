package com.sleepkqq.sololeveling.player.model.repository.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TaskRepository : KRepository<Task, UUID>
