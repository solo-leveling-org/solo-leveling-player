package com.soloist.player.service.task

import com.soloist.player.model.entity.task.Task
import org.babyfish.jimmer.Page

interface TaskService {

	fun getOrInitializeTasks(playerId: Long): List<Task>
	fun createCustomTask(playerId: Long, name: String): Task
	fun getHistory(playerId: Long, page: Int, pageSize: Int): Page<Task>
	fun initializeDailyTasksForAllPlayers(): Int
}
