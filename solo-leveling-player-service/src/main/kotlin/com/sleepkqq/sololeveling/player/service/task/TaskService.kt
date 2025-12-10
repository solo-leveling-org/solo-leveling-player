package com.sleepkqq.sololeveling.player.service.task

import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.enums.TaskTopic
import org.babyfish.jimmer.sql.fetcher.Fetcher
import java.util.UUID

interface TaskService {

	fun find(id: UUID, fetcher: Fetcher<Task> = Fetchers.TASK_FETCHER.allScalarFields()): Task?
	fun get(id: UUID, fetcher: Fetcher<Task> = Fetchers.TASK_FETCHER.allScalarFields()): Task =
		find(id, fetcher) ?: throw ModelNotFoundException(Task::class, id)

	fun updateAll(tasks: Collection<Task>)
	fun insert(task: Task): Task
	fun update(task: Task): Task
	fun findMatchingTasks(playerId: Long, playerTasks: List<PlayerTask>): List<PlayerTask>
	fun initialize(playerTaskTopics: List<PlayerTaskTopic>): Task
	fun deprecateAll(): Int
	fun deprecateByTopic(topic: TaskTopic): Int
}
