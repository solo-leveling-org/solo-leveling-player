package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
@Service
@Profile("test")
class PlayerTaskServiceTestImpl : PlayerTaskService {

	private val playerTasks = ConcurrentHashMap<UUID, PlayerTask>()

	companion object {
		private val CURRENT_TASKS_STATUSES = setOf(
			PlayerTaskStatus.PREPARING,
			PlayerTaskStatus.IN_PROGRESS,
			PlayerTaskStatus.PENDING_COMPLETION
		)
	}

	override fun findPlayerTaskIds(playerId: Long, taskIds: Collection<UUID>): List<UUID> {
		return playerTasks.values
			.filter { it.player.id == playerId && taskIds.contains(it.task.id) }
			.map { it.id }
	}

	override fun insert(playerTask: PlayerTask): PlayerTask {
		if (playerTasks.containsKey(playerTask.id)) {
			throw IllegalStateException("PlayerTask with id ${playerTask.id} already exists")
		}
		playerTasks[playerTask.id] = playerTask
		return playerTask
	}

	override fun update(playerTask: PlayerTask, now: LocalDateTime): PlayerTask {
		if (!playerTasks.containsKey(playerTask.id)) {
			throw IllegalStateException("PlayerTask with id ${playerTask.id} not found")
		}
		val updated = PlayerTask(playerTask) { updatedAt = now }
		playerTasks[playerTask.id] = updated
		return updated
	}

	override fun setStatus(ids: Collection<UUID>, status: PlayerTaskStatus, now: LocalDateTime) {
		ids.forEach { id ->
			val task = playerTasks[id] ?: throw IllegalStateException("PlayerTask with id $id not found")
			playerTasks[id] = PlayerTask(task) {
				this.status = status
				updatedAt = now
			}
		}
	}

	override fun getCurrentTasks(playerId: Long): List<PlayerTask> {
		return playerTasks.values
			.filter { it.player.id == playerId && CURRENT_TASKS_STATUSES.contains(it.status) }
			.toList()
	}

	override fun getCurrentTasksCount(playerId: Long): Long {
		return playerTasks.values
			.count { it.player.id == playerId && CURRENT_TASKS_STATUSES.contains(it.status) }
			.toLong()
	}

	fun clear() = playerTasks.clear()
}
