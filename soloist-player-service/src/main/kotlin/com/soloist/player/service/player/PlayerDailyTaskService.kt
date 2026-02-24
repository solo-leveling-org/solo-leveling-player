package com.soloist.player.service.player

import com.soloist.player.model.entity.player.PlayerDailyTask
import com.soloist.player.model.entity.player.enums.DailyTaskType
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface PlayerDailyTaskService {

	fun insertAll(tasks: Collection<PlayerDailyTask>)
	fun replace(type: DailyTaskType): Long
	fun initialize(playerId: Long, type: DailyTaskType): PlayerDailyTask
	fun findPlayersToInit(type: DailyTaskType): List<Long>
	fun <V : View<PlayerDailyTask>> findView(playerId: Long, viewType: KClass<V>): List<V>
	fun find(playerId: Long, type: DailyTaskType): PlayerDailyTask?
	fun update(task: PlayerDailyTask): PlayerDailyTask
}