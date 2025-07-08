package com.sleepkqq.sololeveling.player.service.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
@Profile("test")
class TaskServiceTestImpl : TaskService {

	private val tasks = ConcurrentHashMap<UUID, Task>()

	override fun get(id: UUID): Task = find(id) ?: throw ModelNotFoundException(Task::class, id)

	override fun find(id: UUID): Task? = tasks[id]

	override fun updateTasks(tasks: Collection<Task>): List<Task> {
		return tasks.map { update(it) }
	}

	override fun insert(task: Task): Task {
		if (tasks.containsKey(task.id)) {
			throw IllegalStateException("Task with id ${task.id} already exists")
		}
		tasks[task.id] = task
		return task
	}

	override fun insertTasks(tasks: Collection<Task>): List<Task> {
		return tasks.map { insert(it) }
	}

	override fun update(task: Task, now: LocalDateTime): Task {
		if (!tasks.containsKey(task.id)) {
			throw IllegalStateException("Task with id ${task.id} not found")
		}
		val updated = Task(task) { updatedAt = now }
		tasks[task.id] = updated
		return updated
	}

	override fun update(task: Task): Task = update(task, LocalDateTime.now())

	fun clear() = tasks.clear()
} 