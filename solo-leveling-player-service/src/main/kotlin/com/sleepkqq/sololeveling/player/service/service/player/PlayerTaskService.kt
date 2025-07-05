package com.sleepkqq.sololeveling.player.service.service.player

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class PlayerTaskService(
	private val playerTaskRepository: PlayerTaskRepository
) {

	companion object {

		private val CURRENT_TASKS_STATUSES = setOf(
			PlayerTaskStatus.PREPARING,
			PlayerTaskStatus.IN_PROGRESS,
			PlayerTaskStatus.PENDING_COMPLETION
		)
	}

	@Transactional(readOnly = true)
	fun getTaskId(playerId: Long, taskId: UUID): UUID =
		playerTaskRepository.findIdByPlayerIdAndTaskId(playerId, taskId)
			?: throw IllegalArgumentException(
				String.format("PlayerTask not found playerId=%d taskId=%s", playerId, taskId)
			)

	@Transactional
	fun insert(playerTask: PlayerTask): PlayerTask =
		playerTaskRepository.save(playerTask, SaveMode.INSERT_ONLY)

	@Transactional
	fun update(playerTask: PlayerTask, now: LocalDateTime): PlayerTask {
		return playerTaskRepository.save(
			PlayerTask(playerTask) { updatedAt = now },
			SaveMode.UPDATE_ONLY
		)
	}

	@Transactional
	fun update(playerTask: PlayerTask): PlayerTask {
		return update(playerTask, LocalDateTime.now())
	}

	@Transactional
	fun setStatus(id: UUID, status: PlayerTaskStatus, now: LocalDateTime = LocalDateTime.now()) {
		playerTaskRepository.setStatus(id, status, now)
	}

	@Transactional(readOnly = true)
	fun getCurrentTasks(playerId: Long): List<PlayerTask> =
		playerTaskRepository.findByPlayerIdAndStatusIn(playerId, CURRENT_TASKS_STATUSES)


	@Transactional(readOnly = true)
	fun getCurrentTasksCount(playerId: Long): Long =
		playerTaskRepository.countByPlayerIdAndStatusIn(playerId, CURRENT_TASKS_STATUSES)
}
