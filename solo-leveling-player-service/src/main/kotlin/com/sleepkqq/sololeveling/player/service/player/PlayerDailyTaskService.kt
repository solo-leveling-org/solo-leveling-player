package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerDailyTask
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.DailyTaskSpec.DailyTaskType
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface PlayerDailyTaskService {

	fun insertAll(tasks: Collection<PlayerDailyTask>)
	fun updateAll(tasks: Collection<PlayerDailyTask>)
	fun replace(task: PlayerDailyTask): PlayerDailyTask
	fun initialize(playerId: Long, type: DailyTaskType): PlayerDailyTask
	fun findPlayersToInit(): List<Long>
	fun <V : View<PlayerDailyTask>> findView(viewType: KClass<V>): List<V>
}