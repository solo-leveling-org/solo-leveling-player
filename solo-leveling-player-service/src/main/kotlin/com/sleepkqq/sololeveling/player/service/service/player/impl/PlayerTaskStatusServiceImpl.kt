package com.sleepkqq.sololeveling.player.service.service.player.impl

import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.player.model.entity.Immutables
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.dto.PlayerView
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.repository.player.PlayerTaskRepository
import com.sleepkqq.sololeveling.player.service.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.service.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.service.service.player.LevelService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerBalanceService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskStatusService
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Suppress("unused")
@Service
class PlayerTaskStatusServiceImpl(
	private val playerTaskRepository: PlayerTaskRepository,
	private val generateTasksProducer: GenerateTasksProducer,
	private val playerBalanceService: PlayerBalanceService,
	private val playerService: PlayerService,
	private val levelService: LevelService,
	private val sendNotificationProducer: SendNotificationProducer
) : PlayerTaskStatusService {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	override fun skipTask(playerTask: PlayerTask, playerId: Long, now: LocalDateTime) {
		setStatus(listOf(playerTask), PlayerTaskStatus.SKIPPED, now)

		generateTasks(playerId, true, setOf(playerTask.order()))
	}

	@Transactional
	override fun pendingCompleteTask(
		playerTask: PlayerTask,
		playerId: Long,
		now: LocalDateTime
	): Pair<PlayerView, PlayerView> {

		setStatus(listOf(playerTask), PlayerTaskStatus.PENDING_COMPLETION, now)

		val playerView = playerService.getView(playerId, PlayerView::class)
		val player = playerView.toEntity()

		val task = playerTask.task()

		val updatedBalance = playerBalanceService.deposit(
			player.balance()!!,
			BigDecimal(task.currencyReward()!!),
			PlayerBalanceTransactionCause.TASK_COMPLETION
		)

		val gainedExperiencePlayer = levelService.gainExperience(
			player,
			task.topics()!!,
			task.experience()!!
		)

		val updatedPlayer = playerService.update(
			Immutables.createPlayer(gainedExperiencePlayer) {
				it.setAgility(player.agility() + task.agility()!!)
				it.setStrength(player.strength() + task.strength()!!)
				it.setIntelligence(player.intelligence() + task.intelligence()!!)
				it.setBalance(updatedBalance)
			},
			now
		)

		sendCompleteTaskNotification(playerId)

		return playerView to PlayerView(updatedPlayer)
	}

	private fun sendCompleteTaskNotification(userId: Long) {
		val txId = UUID.randomUUID().toString()
		try {
			val sendNotificationEvent = SendNotificationEvent(
				txId,
				userId,
				NotificationPriority.LOW,
				Notification(
					null,
					NotificationType.INFO,
					NotificationSource.TASKS,
					false
				)
			)

			sendNotificationProducer.send(sendNotificationEvent)
			log.info("<< Task completion notification sent | txId={}", txId)

		} catch (e: Exception) {
			log.error("Failed to send task completion notification | txId={}", txId, e)
		}
	}

	@Transactional
	override fun inProgressTasks(tasks: Collection<PlayerTask>, now: LocalDateTime) {
		setStatus(tasks, PlayerTaskStatus.IN_PROGRESS, now)
	}

	@Transactional
	override fun completeTasks(tasks: Collection<PlayerTask>, now: LocalDateTime) {
		setStatus(tasks, PlayerTaskStatus.COMPLETED, now)
	}

	@Transactional
	override fun generateTasks(playerId: Long, forReplace: Boolean, replaceOrders: Set<Int>) {
		generateTasksProducer.send(playerId, forReplace, replaceOrders)
	}

	private fun setStatus(
		playerTasks: Collection<PlayerTask>,
		status: PlayerTaskStatus,
		now: LocalDateTime
	) {
		playerTaskRepository.saveEntities(
			playerTasks.map {
				Immutables.createPlayerTask(it) { p ->
					p.setStatus(status)
					p.setUpdatedAt(now)
				}
			},
			SaveMode.UPDATE_ONLY
		)
	}
}
