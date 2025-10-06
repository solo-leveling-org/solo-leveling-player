package com.sleepkqq.sololeveling.player.service.kafka.consumer

import com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds
import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.service.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.service.lozalization.LocalizationCodes.TASKS_GENERATION_SUCCESS
import com.sleepkqq.sololeveling.player.service.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskStatusService
import com.sleepkqq.sololeveling.player.service.service.redis.IdempotencyService
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert

@Suppress("unused")
@Service
class SaveTasksConsumer(
	private val taskService: TaskService,
	private val playerTaskService: PlayerTaskService,
	private val playerTaskStatusService: PlayerTaskStatusService,
	private val sendNotificationProducer: SendNotificationProducer,
	private val avroMapper: AvroMapper,
	private val idempotencyService: IdempotencyService,
	private val messageSource: MessageSource
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@KafkaListener(
		topics = [KafkaTaskTopics.SAVE_TASKS_TOPIC],
		groupId = KafkaGroupIds.PLAYER_GROUP_ID,
		containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
	)
	@Transactional
	fun listen(event: SaveTasksEvent, ack: Acknowledgment) {
		val startTime = System.currentTimeMillis()
		val txId = event.transactionId

		log.info(">> Start processing SaveTasksEvent | transactionId={}", txId)

		try {
			if (idempotencyService.isProcessed(txId)) {
				ack.acknowledge()
				return
			}

			processSaveTasksEvent(event)

			sendSuccessNotification(event)

			val processingTime = System.currentTimeMillis() - startTime
			log.info(
				"<< Successfully processed SaveTasksEvent | transactionId={}, processingTime={}ms",
				txId, processingTime
			)

			ack.acknowledge()

		} catch (e: Exception) {
			val processingTime = System.currentTimeMillis() - startTime
			log.error(
				"Failed to process SaveTasksEvent | transactionId={}, processingTime={}ms, error={}",
				txId, processingTime, e.message, e
			)

			throw e
		}
	}

	private fun processSaveTasksEvent(event: SaveTasksEvent) {
		validateSaveTasksEvent(event)

		val tasks = event.tasks.map {
			avroMapper.map(it).toEntity()
		}

		log.info("Updating {} tasks for player {}", tasks.size, event.playerId)
		taskService.updateAll(tasks)

		val taskIds = tasks.map { it.id() }
		val playerTasks = playerTaskService.find(event.playerId, taskIds)

		if (playerTasks.isNotEmpty()) {
			log.info(
				"Setting {} player tasks to IN_PROGRESS for player {}",
				playerTasks.size,
				event.playerId
			)
			playerTaskStatusService.inProgressTasks(playerTasks)
		}
	}

	private fun validateSaveTasksEvent(event: SaveTasksEvent) {
		Assert.hasText(event.transactionId, "Transaction ID cannot be blank")
		Assert.notEmpty(event.tasks, "Tasks list cannot be empty")
		Assert.isTrue(event.playerId > 0, "Player ID must be positive")

		event.tasks.forEach {
			Assert.hasText(it.taskId, "Task ID cannot be blank")
		}
	}

	private fun sendSuccessNotification(event: SaveTasksEvent) {
		try {
			val sendNotificationEvent = SendNotificationEvent(
				event.transactionId,
				event.playerId,
				NotificationPriority.LOW,
				Notification(
					messageSource.getMessage(
						TASKS_GENERATION_SUCCESS,
						null,
						LocaleContextHolder.getLocale()
					),
					NotificationType.INFO,
					NotificationSource.TASKS
				)
			)

			sendNotificationProducer.send(sendNotificationEvent)
			log.info("Success notification sent for transaction {}", event.transactionId)

		} catch (e: Exception) {
			log.error(
				"Failed to send success notification for transaction {}: {}",
				event.transactionId,
				e.message
			)
		}
	}
}
