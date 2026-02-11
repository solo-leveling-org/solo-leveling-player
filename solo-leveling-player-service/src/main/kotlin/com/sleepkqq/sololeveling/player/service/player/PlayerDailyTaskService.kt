package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.DailyTaskType

interface PlayerDailyTaskService {

	fun insertAll(tasks: Collection<PlayerDailyTask>)
	fun replace(type: DailyTaskType): Long
	fun initialize(playerId: Long, type: DailyTaskType): PlayerDailyTask
	fun findPlayersToInit(type: DailyTaskType): List<Long>
	fun find(playerId: Long, type: DailyTaskType): PlayerDailyTask?
	fun update(task: PlayerDailyTask): PlayerDailyTask
}