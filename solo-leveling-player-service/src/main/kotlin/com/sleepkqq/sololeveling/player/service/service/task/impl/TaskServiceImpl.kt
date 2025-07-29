package com.sleepkqq.sololeveling.player.service.service.task.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
class TaskServiceImpl(
	private val taskRepository: TaskRepository
) : TaskService {

	private companion object {
		const val INITIAL_TASK_VERSION = 0
	}

	@Transactional(readOnly = true)
	override fun get(id: UUID): Task = find(id)
		?: throw ModelNotFoundException(Task::class, id)

	@Transactional(readOnly = true)
	override fun find(id: UUID): Task? = taskRepository.findNullable(id)

	@Transactional
	override fun updateAll(tasks: Collection<Task>, now: LocalDateTime) {
		taskRepository.saveEntities(
			tasks.map { Task(it) { updatedAt = now } },
			SaveMode.UPDATE_ONLY
		)
	}

	@Transactional
	override fun insert(task: Task): Task =
		taskRepository.save(task, SaveMode.INSERT_ONLY)

	@Transactional
	override fun insertAll(tasks: Collection<Task>) {
		taskRepository.saveEntities(tasks, SaveMode.INSERT_ONLY)
	}

	@Transactional
	override fun update(task: Task, now: LocalDateTime): Task = taskRepository.save(
		Task(task) { updatedAt = now },
		SaveMode.UPDATE_ONLY
	)

	override fun initialize(playerId: Long): Task = Task {
		id = UUID.randomUUID()
		version = INITIAL_TASK_VERSION
		playerTasks = listOf(
			PlayerTask {
				id = UUID.randomUUID()
				status = PlayerTaskStatus.PREPARING
				this.playerId = playerId
			}
		)
	}
}
