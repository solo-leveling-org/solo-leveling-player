package com.soloist.player.service.player

import com.soloist.player.model.entity.task.DailyTask
import com.soloist.player.model.entity.task.enums.DailyTaskType
import org.babyfish.jimmer.View
import kotlin.reflect.KClass

interface DailyTaskService {

	fun insertAll(tasks: Collection<DailyTask>)
	fun replace(type: DailyTaskType): Long
	fun initialize(playerId: Long, type: DailyTaskType): DailyTask
	fun findPlayersToInit(type: DailyTaskType): List<Long>
	fun <V : View<DailyTask>> findView(playerId: Long, viewType: KClass<V>): List<V>
	fun find(playerId: Long, type: DailyTaskType): DailyTask?
	fun update(task: DailyTask): DailyTask
}