package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import com.sleepkqq.sololeveling.player.service.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskStatusService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Suppress("unused")
@Service
class PlayerTaskStatusServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository,
	private val generateTasksProducer: GenerateTasksProducer
) : PlayerTaskStatusService {

	@Transactional
	override fun skipTask(playerTask: PlayerTask, playerId: Long, now: LocalDateTime) {
		setStatus(listOf(playerTask), PlayerTaskStatus.SKIPPED, now)

		generateTasks(playerId)
	}

	@Transactional
	override fun pendingCompleteTask(playerTask: PlayerTask, now: LocalDateTime) {
		setStatus(listOf(playerTask), PlayerTaskStatus.PENDING_COMPLETION, now)
	}

	@Transactional
	override fun inProgressTasks(tasks: Collection<PlayerTask>, now: LocalDateTime) {
		setStatus(tasks, PlayerTaskStatus.IN_PROGRESS, now)
	}


	@Transactional
	override fun generateTasks(playerId: Long) {
		generateTasksProducer.send(playerId)
	}

	private fun setStatus(
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
}
