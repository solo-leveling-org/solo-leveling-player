package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView

interface PlayerTaskStatusService {

	fun skipTask(playerTask: PlayerTask, playerId: Long)
	fun completeTask(playerTask: PlayerTask, playerId: Long): Pair<PlayerView, PlayerView>
	fun inProgressTasks(tasks: Collection<PlayerTask>)
	fun completeTasks(tasks: Collection<PlayerTask>)
	fun generateTasks(
		playerId: Long,
		replaceOrders: Set<Int> = setOf()
	)
}
