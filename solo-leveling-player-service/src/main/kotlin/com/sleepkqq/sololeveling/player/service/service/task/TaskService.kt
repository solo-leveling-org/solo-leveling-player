package com.sleepkqq.sololeveling.player.service.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class TaskService(
	private val taskRepository: TaskRepository
) {

	@Transactional(readOnly = true)
	fun get(id: UUID): Task = find(id)
		?: throw ModelNotFoundException(Task::class, id)

	@Transactional(readOnly = true)
	fun find(id: UUID): Task? = taskRepository.findNullable(id)

	@Transactional
	fun updateTasks(tasks: Collection<Task>) = taskRepository.updateTasks(tasks)

	@Transactional
	fun insert(task: Task): Task =
		taskRepository.save(task, SaveMode.INSERT_ONLY)

	@Transactional
	fun insertTasks(tasks: Collection<Task>) =
		taskRepository.saveEntities(tasks, SaveMode.INSERT_ONLY)

	@Transactional
	fun update(task: Task, now: LocalDateTime): Task = taskRepository.save(
		Task(task) { updatedAt = now },
		SaveMode.UPDATE_ONLY
	)

	@Transactional
	fun update(task: Task): Task = update(task, LocalDateTime.now())
}