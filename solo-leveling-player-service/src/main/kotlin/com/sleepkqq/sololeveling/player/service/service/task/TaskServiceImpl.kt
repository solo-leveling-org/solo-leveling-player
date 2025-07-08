package com.sleepkqq.sololeveling.player.service.service.task

import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Profile("!test")
class TaskServiceImpl(
	private val taskRepository: TaskRepository
) : TaskService {

	@Transactional(readOnly = true)
	override fun get(id: UUID): Task = find(id)
		?: throw ModelNotFoundException(Task::class, id)

	@Transactional(readOnly = true)
	override fun find(id: UUID): Task? = taskRepository.findNullable(id)

	@Transactional
	override fun updateTasks(tasks: Collection<Task>) = taskRepository.updateTasks(tasks)

	@Transactional
	override fun insert(task: Task): Task =
		taskRepository.save(task, SaveMode.INSERT_ONLY)

	@Transactional
	override fun insertTasks(tasks: Collection<Task>) =
		taskRepository.saveEntities(tasks, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(task: Task, now: LocalDateTime): Task = taskRepository.save(
		Task(task) { updatedAt = now },
		SaveMode.UPDATE_ONLY
	)

	@Transactional
	override fun update(task: Task): Task = update(task, LocalDateTime.now())
}