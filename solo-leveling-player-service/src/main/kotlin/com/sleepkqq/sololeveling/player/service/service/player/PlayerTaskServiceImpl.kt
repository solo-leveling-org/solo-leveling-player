package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
@Profile("!test")
class PlayerTaskServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository
) : PlayerTaskService {

	companion object {
		private val CURRENT_TASKS_STATUSES = setOf(
			PlayerTaskStatus.PREPARING,
			PlayerTaskStatus.IN_PROGRESS,
			PlayerTaskStatus.PENDING_COMPLETION
		)
	}

	@Transactional(readOnly = true)
	override fun findPlayerTaskIds(playerId: Long, taskIds: Collection<UUID>): List<UUID> =
		playerTaskRepository.findIdByPlayerIdAndTaskIdsIn(playerId, taskIds)

	@Transactional
	override fun insert(playerTask: PlayerTask): PlayerTask =
		playerTaskRepository.save(playerTask, SaveMode.INSERT_ONLY)

	@Transactional
	override fun update(playerTask: PlayerTask, now: LocalDateTime): PlayerTask {
		return playerTaskRepository.save(
			PlayerTask(playerTask) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)
	}

	@Transactional
	override fun setStatus(
		ids: Collection<UUID>,
		status: PlayerTaskStatus,
		now: LocalDateTime
	) {
		playerTaskRepository.setStatus(ids, status, now)
	}

	@Transactional(readOnly = true)
	override fun getCurrentTasks(playerId: Long): List<PlayerTask> =
		playerTaskRepository.findByPlayerIdAndStatusIn(playerId, CURRENT_TASKS_STATUSES)

	@Transactional(readOnly = true)
	override fun getCurrentTasksCount(playerId: Long): Long =
		playerTaskRepository.countByPlayerIdAndStatusIn(playerId, CURRENT_TASKS_STATUSES)
} 