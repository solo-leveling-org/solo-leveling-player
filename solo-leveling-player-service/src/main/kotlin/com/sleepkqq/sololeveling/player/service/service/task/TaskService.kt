package com.sleepkqq.sololeveling.player.service.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import java.time.LocalDateTime
import java.util.UUID

interface TaskService {
	fun get(id: UUID): Task
	fun find(id: UUID): Task?
	fun updateAll(tasks: Collection<Task>, now: LocalDateTime = LocalDateTime.now())
	fun insert(task: Task): Task
	fun insertAll(tasks: Collection<Task>)
	fun update(task: Task, now: LocalDateTime = LocalDateTime.now()): Task
} 