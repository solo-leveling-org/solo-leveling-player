package com.sleepkqq.sololeveling.player.service.kafka.consumer

import com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds
import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.service.kafka.producer.DeadLetterQueueProducer
import com.sleepkqq.sololeveling.player.service.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.service.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class SaveTasksConsumer(
	private val taskService: TaskService,
	private val playerTaskService: PlayerTaskService,
	private val sendNotificationProducer: SendNotificationProducer,
	private val deadLetterQueueProducer: DeadLetterQueueProducer,
	private val avroMapper: AvroMapper
) {

	private val log = LoggerFactory.getLogger(SaveTasksConsumer::class.java)

	@KafkaListener(
		topics = [KafkaTaskTopics.SAVE_TASKS_TOPIC],
		groupId = KafkaGroupIds.PLAYER_GROUP_ID,
		containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
	)
	@Transactional
	fun listen(
		event: SaveTasksEvent,
		record: ConsumerRecord<String, SaveTasksEvent>,
		ack: Acknowledgment,
		@Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
		@Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
		@Header(KafkaHeaders.OFFSET) offset: Long
	) {
		val startTime = System.currentTimeMillis()
		log.info(">> Start processing SaveTasksEvent | transactionId={}, topic={}, partition={}, offset={}",
			event.transactionId, topic, partition, offset)

		try {
			processSaveTasksEvent(event)
			
			val processingTime = System.currentTimeMillis() - startTime
			log.info("<< Successfully processed SaveTasksEvent | transactionId={}, processingTime={}ms",
				event.transactionId, processingTime)
			
			ack.acknowledge()
			
		} catch (e: Exception) {
			val processingTime = System.currentTimeMillis() - startTime
			log.error("Failed to process SaveTasksEvent | transactionId={}, processingTime={}ms, error={}",
				event.transactionId, processingTime, e.message, e)
			
			handleFailedMessage(record, e)
			throw e
		}
	}

	private fun processSaveTasksEvent(event: SaveTasksEvent) {
		val now = LocalDateTime.now()
		
		validateSaveTasksEvent(event)
		
		val tasks = event.tasks.map {
			Task(avroMapper.map(it)) { updatedAt = now }
		}
		
		log.debug("Updating {} tasks for player {}", tasks.size, event.playerId)
		taskService.updateTasks(tasks)

		val taskIds = event.tasks.map { UUID.fromString(it.taskId) }
		val playerTaskIds = playerTaskService.findPlayerTaskIds(
			event.playerId,
			taskIds
		)
		
		if (playerTaskIds.isNotEmpty()) {
			log.debug("Setting {} player tasks to IN_PROGRESS for player {}", playerTaskIds.size, event.playerId)
			playerTaskService.setStatus(playerTaskIds, PlayerTaskStatus.IN_PROGRESS, now)
		}

		sendSuccessNotification(event)
	}

	private fun validateSaveTasksEvent(event: SaveTasksEvent) {
		require(event.transactionId.isNotBlank()) { "Transaction ID cannot be blank" }
		require(event.playerId > 0) { "Player ID must be positive" }
		require(event.tasks.isNotEmpty()) { "Tasks list cannot be empty" }
		
		event.tasks.forEach { task ->
			require(task.taskId.isNotBlank()) { "Task ID cannot be blank" }
		}
	}

	private fun sendSuccessNotification(event: SaveTasksEvent) {
		try {
			val sendNotificationEvent = SendNotificationEvent(
				event.transactionId,
				event.playerId,
				NotificationPriority.LOW,
				Notification("Your tasks have been successfully generated!")
			)
			sendNotificationProducer.send(sendNotificationEvent)
			log.debug("Success notification sent for transaction {}", event.transactionId)
		} catch (e: Exception) {
			log.warn("Failed to send success notification for transaction {}: {}", event.transactionId, e.message)
		}
	}

	private fun handleFailedMessage(record: ConsumerRecord<String, SaveTasksEvent>, exception: Exception) {
		try {
			val headers = mapOf(
				"originalTopic" to record.topic(),
				"partition" to record.partition().toString(),
				"offset" to record.offset().toString(),
				"timestamp" to record.timestamp().toString()
			)
			
			deadLetterQueueProducer.sendToDlq(
				record.topic(),
				record.key(),
				record.value().toString(),
				exception,
				headers
			)
		} catch (dlqException: Exception) {
			log.error("Failed to send message to DLQ: {}", dlqException.message, dlqException)
		}
	}
}
