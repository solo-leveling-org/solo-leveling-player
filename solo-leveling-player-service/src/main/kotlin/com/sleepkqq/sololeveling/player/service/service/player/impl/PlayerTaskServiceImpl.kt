package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerTaskView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
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
		playerTaskRepository.findByPlayerIdAndTaskIdsIn(playerId, taskIds)

	@Transactional
	override fun insert(playerTask: PlayerTask): PlayerTask =
		playerTaskRepository.save(playerTask, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(playerTask: PlayerTask, now: LocalDateTime): PlayerTask =
		playerTaskRepository.save(
			PlayerTask(playerTask) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)

	@Transactional
	override fun setStatus(
		playerTasks: Collection<PlayerTask>,
		status: PlayerTaskStatus,
		now: LocalDateTime
	) {
		playerTaskRepository.saveEntities(
			playerTasks.map {
				PlayerTask(it) {
					this.status = status
					updatedAt = now
				}
			},
			SaveMode.UPDATE_ONLY
		)
	}

	@Transactional(readOnly = true)
	override fun getActiveTasks(playerId: Long): List<PlayerTaskView> =
		playerTaskRepository.findByPlayerIdAndStatusIn(playerId, ACTIVE_TASKS_STATUSES)

	@Transactional(readOnly = true)
	override fun getTasksCount(playerId: Long): Long =
		playerTaskRepository.getTasksCountByPlayerId(playerId)

	@Transactional(readOnly = true)
	override fun getActiveTasksCount(playerId: Long): Long =
		playerTaskRepository.countByPlayerIdAndStatusIn(playerId, ACTIVE_TASKS_STATUSES)
}
