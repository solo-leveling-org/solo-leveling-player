package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import java.time.LocalDateTime

interface PlayerTaskStatusService {

	fun skipTask(playerTask: PlayerTask, playerId: Long, now: LocalDateTime = LocalDateTime.now())
	fun pendingCompleteTask(playerTask: PlayerTask, now: LocalDateTime = LocalDateTime.now())
	fun inProgressTasks(tasks: Collection<PlayerTask>, now: LocalDateTime = LocalDateTime.now())
	fun generateTasks(playerId: Long)
}
