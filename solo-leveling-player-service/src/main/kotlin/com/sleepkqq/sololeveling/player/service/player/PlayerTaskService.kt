package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import java.util.UUID

interface PlayerTaskService {

	fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>
	fun insert(playerTask: PlayerTask): PlayerTask
	fun update(playerTask: PlayerTask): PlayerTask
	fun insertAll(playerTasks: Collection<PlayerTask>)
	fun getActiveTasks(playerId: Long): List<PlayerTaskView>
	fun getActiveTasksCount(playerId: Long): Long
	fun initialize(playerId: Long, order: Int): PlayerTask
	fun skipTask(playerTask: PlayerTask, playerId: Long)
	fun completeTask(playerTask: PlayerTask, playerId: Long): Pair<PlayerView, PlayerView>
	fun inProgressTasks(tasks: Collection<PlayerTask>)
	fun generateTasks(
		playerId: Long,
		replaceOrders: Set<Int> = setOf()
	)
}
