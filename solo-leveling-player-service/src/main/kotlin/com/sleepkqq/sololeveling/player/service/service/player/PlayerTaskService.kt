package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import java.time.LocalDateTime
import java.util.UUID

interface PlayerTaskService {
	fun findPlayerTaskIds(playerId: Long, taskIds: Collection<UUID>): List<UUID>
	fun insert(playerTask: PlayerTask): PlayerTask
	fun update(playerTask: PlayerTask, now: LocalDateTime = LocalDateTime.now()): PlayerTask
	fun setStatus(
		ids: Collection<UUID>,
		status: PlayerTaskStatus,
		now: LocalDateTime = LocalDateTime.now()
	)

	fun getCurrentTasks(playerId: Long): List<PlayerTask>
	fun getCurrentTasksCount(playerId: Long): Long
}
