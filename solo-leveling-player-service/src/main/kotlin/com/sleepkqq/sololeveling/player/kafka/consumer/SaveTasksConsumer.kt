package com.sleepkqq.sololeveling.player.kafka.consumer

import com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds
import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.idempotency.IdempotencyService
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.model.entity.task.dto.TaskInput
import com.sleepkqq.sololeveling.player.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.dao.TransientDataAccessException
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.util.UUID

@Suppress("unused")
@Service
class SaveTasksConsumer(
	private val taskService: TaskService,
	private val playerTaskService: PlayerTaskService,
	private val avroMapper: AvroMapper,
	private val idempotencyService: IdempotencyService,
	private val notificationService: NotificationService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@RetryableTopic(
		attempts = "4",
		backoff = Backoff(
			delay = 3000,
			multiplier = 2.0,
			maxDelay = 30000
		),
		autoCreateTopics = "true",
		retryTopicSuffix = "-retry",
		dltTopicSuffix = "-dlt",
		include = [SocketTimeoutException::class, TransientDataAccessException::class],
		dltStrategy = DltStrategy.FAIL_ON_ERROR,
		kafkaTemplate = "kafkaTemplateSaveTasksEvent"
	)
	@KafkaListener(
		topics = [KafkaTaskTopics.SAVE_TASKS_TOPIC],
		groupId = KafkaGroupIds.PLAYER_GROUP_ID,
		containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
	)
	@Transactional
	fun listen(event: SaveTasksEvent, ack: Acknowledgment) {
		val txId = event.txId

		log.info(">> Start processing SaveTasksEvent | txId={}", txId)

		try {
			// Атомарная проверка идемпотентности
			if (!idempotencyService.tryAcquireLock(txId)) {
				log.info("Message already processed or being processed | txId={}", txId)
				ack.acknowledge()
				return
			}

			processSaveTasksEvent(event)
			notificationService.send(NotificationCommand.SaveTasks(event))

			// Помечаем как успешно обработанное
			idempotencyService.markAsProcessed(txId)

			log.info("Successfully processed SaveTasksEvent | txId={}", txId)
			ack.acknowledge()

		} catch (e: Exception) {
			log.error("Failed to process SaveTasksEvent | txId={}", txId, e)
			// Освобождаем блокировку при ошибке для повторной попытки
			idempotencyService.releaseLock(txId)
			throw e // Пробрасываем для retry механизма
		}
	}

	@DltHandler
	fun handleDlt(
		event: SaveTasksEvent,
		@Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
		@Header(KafkaHeaders.ORIGINAL_OFFSET) offsetBytes: ByteArray,
		@Header(KafkaHeaders.EXCEPTION_MESSAGE) errorMessage: String,
		@Header(KafkaHeaders.EXCEPTION_FQCN) exceptionClass: String,
		@Header(KafkaHeaders.EXCEPTION_STACKTRACE) stackTrace: String
	) {
		val offset = ByteBuffer.wrap(offsetBytes).long
		val txId = event.txId

		log.error(
			"""
            |========================================
            |Message moved to DLT after all retries exhausted
            |txId: $txId
            |playerId: ${event.playerId}
            |originalTopic: $topic
            |originalOffset: $offset
            |exceptionClass: $exceptionClass
            |errorMessage: $errorMessage
            |========================================
            """.trimMargin()
		)

		// Сохраняем детали в БД для ручного анализа
		saveFailedMessageToDatabase(
			event = event,
			topic = topic,
			offset = offset,
			errorMessage = errorMessage,
			exceptionClass = exceptionClass,
			stackTrace = stackTrace
		)

		// Отправляем алерт в мониторинг
		sendAlert(
			message = "DLT: SaveTasksEvent | txId=$txId | error=$errorMessage",
			severity = "HIGH"
		)

		// Освобождаем блокировку идемпотентности
		idempotencyService.releaseLock(txId)

		log.info("DLT message processed and saved to database | txId={}", txId)
	}

	private fun saveFailedMessageToDatabase(
		event: SaveTasksEvent,
		topic: String,
		offset: Long,
		errorMessage: String,
		exceptionClass: String,
		stackTrace: String
	) {
		// TODO: Реализовать сохранение в таблицу failed_messages
		// Пример структуры:
		// - id (UUID)
		// - tx_id (String)
		// - event_type (String)
		// - event_payload (JSONB)
		// - original_topic (String)
		// - original_offset (Long)
		// - error_message (Text)
		// - exception_class (String)
		// - stack_trace (Text)
		// - created_at (Timestamp)
		// - processed (Boolean)

		log.warn("TODO: Implement saveFailedMessageToDatabase for txId={}", event.txId)
	}

	private fun sendAlert(message: String, severity: String) {
		// TODO: Интеграция с системой мониторинга
		// Примеры: Sentry, Slack, PagerDuty, etc.

		log.warn("TODO: Implement alerting. Message: {}, Severity: {}", message, severity)
	}

	private fun processSaveTasksEvent(event: SaveTasksEvent) {
		val tasks = event.tasks.map(avroMapper::map)
			.onEach {
				it.title!!.id = UUID.randomUUID()
				it.description!!.id = UUID.randomUUID()
			}
			.map(TaskInput::toEntity)

		log.info("Updating {} tasks for player {}", tasks.size, event.playerId)
		taskService.updateAll(tasks)

		val taskIds = tasks.map { it.id() }
		val playerTasks = playerTaskService.find(event.playerId, taskIds)

		if (playerTasks.isNotEmpty()) {
			log.info(
				"Setting {} player tasks to IN_PROGRESS for player {}",
				playerTasks.size, event.playerId
			)
			playerTaskService.inProgressTasks(playerTasks)
		}
	}
}
