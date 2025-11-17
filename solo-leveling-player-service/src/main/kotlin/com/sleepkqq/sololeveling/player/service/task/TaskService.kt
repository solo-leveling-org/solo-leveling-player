package com.sleepkqq.sololeveling.player.service.task

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import java.util.*

interface TaskService {

	fun get(id: UUID): Task
	fun find(id: UUID): Task?
	fun updateAll(tasks: Collection<Task>)
	fun insert(task: Task): Task
	fun update(task: Task): Task
	fun generateTasks(playerId: Long, tasks: List<Task>)
	fun initialize(playerTaskTopics: List<PlayerTaskTopic>): Task
}
