package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.service.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskStatusService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Suppress("unused")
@Service
class PlayerTaskStatusServiceImpl(
	private val playerTaskService: PlayerTaskService,
	private val generateTasksProducer: GenerateTasksProducer
) : PlayerTaskStatusService {

	@Transactional
	override fun skipTask(playerTask: PlayerTask, playerId: Long, now: LocalDateTime) {
		playerTaskService.setStatus(listOf(playerTask), PlayerTaskStatus.SKIPPED, now)

		generateTasks(playerId)
	}

	@Transactional
	override fun pendingCompleteTask(playerTask: PlayerTask, now: LocalDateTime) {
		playerTaskService.setStatus(listOf(playerTask), PlayerTaskStatus.PENDING_COMPLETION, now)
	}

	@Transactional
	override fun generateTasks(playerId: Long) {
		generateTasksProducer.send(playerId)
	}
}
