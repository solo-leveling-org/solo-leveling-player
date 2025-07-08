package com.sleepkqq.sololeveling.player.service.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import java.time.LocalDateTime
import java.util.UUID

interface TaskService {
	fun get(id: UUID): Task
	fun find(id: UUID): Task?
	fun updateTasks(tasks: Collection<Task>): Any
	fun insert(task: Task): Task
	fun insertTasks(tasks: Collection<Task>): Any
	fun update(task: Task, now: LocalDateTime): Task
	fun update(task: Task): Task
} 