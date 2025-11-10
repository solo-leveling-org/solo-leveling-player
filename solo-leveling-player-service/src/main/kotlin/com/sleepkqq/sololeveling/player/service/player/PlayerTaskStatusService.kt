package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import java.time.LocalDateTime

interface PlayerTaskStatusService {

	fun skipTask(playerTask: PlayerTask, playerId: Long, now: LocalDateTime = LocalDateTime.now())
	fun pendingCompleteTask(
		playerTask: PlayerTask,
		playerId: Long,
		now: LocalDateTime = LocalDateTime.now()
	): Pair<PlayerView, PlayerView>

	fun inProgressTasks(tasks: Collection<PlayerTask>, now: LocalDateTime = LocalDateTime.now())
	fun completeTasks(tasks: Collection<PlayerTask>, now: LocalDateTime = LocalDateTime.now())
	fun generateTasks(
		playerId: Long,
		replaceOrders: Set<Int> = setOf()
	)
}
