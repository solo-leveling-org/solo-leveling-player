package com.soloist.player.service.task

import com.soloist.player.model.entity.player.PlayerTask
import com.soloist.player.model.entity.player.PlayerTaskTopic
import com.soloist.player.model.entity.task.Task
import com.soloist.player.model.entity.task.dto.VectorizeTaskView
import com.soloist.player.model.entity.task.enums.TaskTopic
import org.babyfish.jimmer.Page

interface TaskService {

	fun updateAll(tasks: Collection<Task>)
	fun insert(task: Task): Task
	fun update(task: Task): Task
	fun findMatchingTasks(playerId: Long, playerTasks: List<PlayerTask>): List<PlayerTask>
	fun initialize(playerTaskTopics: List<PlayerTaskTopic>): Task
	fun deprecateAll(): Int
	fun deprecateByTopic(topic: TaskTopic): Int
	fun findToVectorize(page: Int, pageSize: Int): Page<VectorizeTaskView>
}
