package com.soloist.player.service.player

import com.soloist.avro.task.SaveTasksOperation
import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.Fetchers
import com.soloist.player.model.entity.player.Player
import com.soloist.player.model.entity.player.PlayerTask
import com.soloist.player.model.entity.player.PlayerTaskFetcher
import com.soloist.player.model.entity.player.dto.PlayerView
import com.soloist.player.model.entity.player.dto.PreparingPlayerTaskView
import com.soloist.player.model.entity.task.Task
import com.soloist.proto.player.RequestPaging
import com.soloist.proto.player.RequestQueryOptions
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
	fun generateTasks(
		playerId: Long,
		player: Player? = null,
		replaceOrders: Set<Int> = setOf(),
		operation: SaveTasksOperation
	)

	fun <V : View<PlayerTask>> searchView(
		playerId: Long,
		options: RequestQueryOptions,
		paging: RequestPaging,
		viewType: KClass<V>
	): Page<V>
}
