package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import java.util.UUID

interface PlayerTaskService {

	fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>
	fun insert(playerTask: PlayerTask): PlayerTask
	fun update(playerTask: PlayerTask): PlayerTask
	fun insertAll(playerTasks: Collection<PlayerTask>)
	fun getActiveTasks(playerId: Long): List<PlayerTaskView>
	fun getActiveTasksCount(playerId: Long): Long
	fun initialize(playerId: Long, order: Int): PlayerTask
}
