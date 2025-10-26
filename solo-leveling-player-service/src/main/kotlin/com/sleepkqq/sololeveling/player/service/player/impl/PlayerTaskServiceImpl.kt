package com.sleepkqq.sololeveling.player.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerCompletionTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
class PlayerTaskServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository
) : PlayerTaskService {

	private companion object {
		val ACTIVE_TASKS_STATUSES = setOf(
			PlayerTaskStatus.PREPARING,
			PlayerTaskStatus.IN_PROGRESS,
			PlayerTaskStatus.PENDING_COMPLETION
		)
	}

	@Transactional(readOnly = true)
	override fun find(playerId: Long, taskIds: Collection<UUID>): List<PlayerTask> =
		playerTaskRepository.findByPlayerIdAndTaskIdIn(playerId, taskIds)

	@Transactional
	override fun insert(playerTask: PlayerTask): PlayerTask =
		playerTaskRepository.save(playerTask, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(playerTask: PlayerTask, now: LocalDateTime): PlayerTask =
		playerTaskRepository.save(
			Immutables.createPlayerTask(playerTask) {
				it.setUpdatedAt(now)
			},
			SaveMode.UPDATE_ONLY
		)

	@Transactional
	override fun insertAll(playerTasks: Collection<PlayerTask>) {
		playerTaskRepository.saveEntities(playerTasks, SaveMode.INSERT_ONLY)
	}

	@Transactional(readOnly = true)
	override fun getActiveTasks(playerId: Long): List<PlayerTaskView> =
		playerTaskRepository.findByPlayerIdAndStatusIn(
			playerId,
			ACTIVE_TASKS_STATUSES,
			PlayerTaskView::class.java
		)

	@Transactional(readOnly = true)
	override fun getPendingCompletionTasks(): List<PlayerCompletionTask> =
		playerTaskRepository.findByStatus(
			PlayerTaskStatus.PENDING_COMPLETION,
			PlayerCompletionTask::class.java
		)

	@Transactional(readOnly = true)
	override fun getActiveTasksCount(playerId: Long): Long =
		playerTaskRepository.countByPlayerIdAndStatusIn(playerId, ACTIVE_TASKS_STATUSES)

	override fun initialize(playerId: Long, order: Int): PlayerTask = Immutables.createPlayerTask {
		it.setId(UUID.randomUUID())
		it.setStatus(PlayerTaskStatus.PREPARING)
		it.setOrder(order)
		it.setPlayerId(playerId)
		it.setTask(
			Immutables.createTask { t ->
				t.setId(UUID.randomUUID())
				t.setVersion(0)
			}
		)
	}
}
