package com.sleepkqq.sololeveling.player.service.task.impl

import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.repository.task.TaskRepository
import com.sleepkqq.sololeveling.player.service.task.TaskService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TaskServiceImpl(
	private val taskRepository: TaskRepository
) : TaskService {

	@Transactional(readOnly = true)
	override fun get(id: UUID): Task = find(id)
		?: throw ModelNotFoundException(Task::class, id)

	@Transactional(readOnly = true)
	override fun find(id: UUID): Task? = taskRepository.findNullable(id)

	@Transactional
	override fun updateAll(tasks: Collection<Task>) {
		taskRepository.saveEntities(tasks, SaveMode.UPDATE_ONLY)
	}

	@Transactional
	override fun insert(task: Task): Task =
		taskRepository.save(task, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(task: Task): Task =
		taskRepository.save(task, SaveMode.UPDATE_ONLY)
}
