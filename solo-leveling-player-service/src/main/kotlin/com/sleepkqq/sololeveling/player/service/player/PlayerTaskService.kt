package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerCompletionTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import java.time.LocalDateTime
import java.util.UUID

interface PlayerTaskService {

	fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>
	fun insert(playerTask: PlayerTask): PlayerTask
	fun update(playerTask: PlayerTask, now: LocalDateTime = LocalDateTime.now()): PlayerTask
	fun insertAll(playerTasks: Collection<PlayerTask>)
	fun getActiveTasks(playerId: Long): List<PlayerTaskView>
	fun getPendingCompletionTasks(): List<PlayerCompletionTask>
	fun getActiveTasksCount(playerId: Long): Long
	fun initialize(playerId: Long, order: Int): PlayerTask
}
