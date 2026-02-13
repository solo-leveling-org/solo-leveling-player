package com.sleepkqq.sololeveling.player.service.player

import com.sleepkqq.sololeveling.player.exception.ModelNotFoundException
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.player.Player
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskFetcher
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PreparingPlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.proto.player.RequestPaging
import com.sleepkqq.sololeveling.proto.player.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import java.util.*
import kotlin.reflect.KClass

interface PlayerTaskService {

	fun find(
		id: UUID,
		fetcher: PlayerTaskFetcher = Fetchers.PLAYER_TASK_FETCHER.allScalarFields()
	): PlayerTask?

	fun get(
		id: UUID,
		fetcher: PlayerTaskFetcher = Fetchers.PLAYER_TASK_FETCHER.allScalarFields()
	): PlayerTask = find(id, fetcher) ?: throw ModelNotFoundException(PlayerTask::class, id)

	fun <V : View<PlayerTask>> findView(id: UUID, viewType: KClass<V>): V?
	fun <V : View<PlayerTask>> getView(id: UUID, viewType: KClass<V>): V = findView(id, viewType)
		?: throw ModelNotFoundException(PlayerTask::class, id)

	fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>
	fun insertAll(playerTasks: Collection<PlayerTask>)
	fun <V : View<PlayerTask>> getActiveTasks(playerId: Long, viewType: KClass<V>): List<V>
	fun getPreparingTasksForRetry(): List<PreparingPlayerTaskView>
	fun initialize(playerId: Long, order: Int, task: Task): PlayerTask
	fun skipTask(playerId: Long, id: UUID)
	fun completeTask(playerId: Long, id: UUID): Pair<PlayerView, PlayerView>
	fun inProgressTasks(tasks: Collection<PlayerTask>)
	fun generateTasks(playerId: Long, player: Player? = null, replaceOrders: Set<Int> = setOf())
	fun <V : View<PlayerTask>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	): Page<V>
}
