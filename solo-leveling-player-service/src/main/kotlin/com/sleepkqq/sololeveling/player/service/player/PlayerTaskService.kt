package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import java.util.UUID
import kotlin.reflect.KClass

interface PlayerTaskService {

	fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>
	fun insert(playerTask: PlayerTask): PlayerTask
	fun update(playerTask: PlayerTask): PlayerTask
	fun insertAll(playerTasks: Collection<PlayerTask>)
	fun getActiveTasks(playerId: Long): List<PlayerTaskView>
	fun getActiveTasksCount(playerId: Long): Long
	fun initialize(playerId: Long, order: Int): PlayerTask
	fun skipTask(playerId: Long, playerTask: PlayerTask)
	fun completeTask(
		playerId: Long,
		playerTask: PlayerTask,
		task: Task
	): Pair<PlayerView, PlayerView>

	fun inProgressTasks(tasks: Collection<PlayerTask>)
	fun generateTasks(
		playerId: Long,
		replaceOrders: Set<Int> = setOf()
	)

	fun <V : View<PlayerTask>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		viewType: KClass<V>
	): Page<V>
}
