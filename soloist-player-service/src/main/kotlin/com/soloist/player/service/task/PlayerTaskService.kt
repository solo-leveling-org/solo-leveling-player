package com.soloist.player.service.task

import com.soloist.avro.task.SaveTasksOperation
import com.soloist.player.exception.ModelNotFoundException
import com.soloist.player.model.entity.player.Player
import com.soloist.player.model.entity.task.PlayerTask
import com.soloist.player.model.entity.player.dto.CompleteTaskPlayerView
import com.soloist.player.model.entity.task.dto.PreparingPlayerTaskView
import com.soloist.player.model.entity.task.Task
import com.soloist.proto.common.RequestPaging
import com.soloist.proto.common.RequestQueryOptions
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import java.util.*
import kotlin.reflect.KClass

interface PlayerTaskService {

	fun <V : View<PlayerTask>> findView(id: UUID, viewType: KClass<V>): V?
	fun <V : View<PlayerTask>> getView(id: UUID, viewType: KClass<V>): V = findView(id, viewType)
		?: throw ModelNotFoundException(PlayerTask::class, id)

	fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask>
	fun insertAll(playerTasks: Collection<PlayerTask>)
	fun <V : View<PlayerTask>> getActiveTasks(playerId: Long, viewType: KClass<V>): List<V>
	fun getPreparingTasksForRetry(): List<PreparingPlayerTaskView>
	fun initialize(playerId: Long, order: Int, task: Task): PlayerTask
	fun skipTask(playerId: Long, id: UUID)
	fun completeTask(playerId: Long, id: UUID): Pair<CompleteTaskPlayerView, CompleteTaskPlayerView>
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
