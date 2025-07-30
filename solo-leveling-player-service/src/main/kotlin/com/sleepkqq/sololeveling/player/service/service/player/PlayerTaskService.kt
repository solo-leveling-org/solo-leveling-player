package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import java.time.LocalDateTime
import java.util.UUID

interface PlayerTaskService {
	fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>
	fun insert(playerTask: PlayerTask): PlayerTask
	fun update(playerTask: PlayerTask, now: LocalDateTime = LocalDateTime.now()): PlayerTask
	fun setStatus(
		playerTasks: Collection<PlayerTask>,
		status: PlayerTaskStatus,
		now: LocalDateTime = LocalDateTime.now()
	)
	fun skipTask(playerTask: PlayerTask, playerId: Long)

	fun getActiveTasks(playerId: Long): List<PlayerTaskView>
	fun getTasksCount(playerId: Long): Long
	fun getActiveTasksCount(playerId: Long): Long
}
