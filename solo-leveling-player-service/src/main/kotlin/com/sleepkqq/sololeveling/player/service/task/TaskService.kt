package com.sleepkqq.sololeveling.player.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import java.util.UUID

interface TaskService {

	fun get(id: UUID): Task
	fun find(id: UUID): Task?
	fun updateAll(tasks: Collection<Task>)
	fun insert(task: Task): Task
	fun update(task: Task): Task
}
