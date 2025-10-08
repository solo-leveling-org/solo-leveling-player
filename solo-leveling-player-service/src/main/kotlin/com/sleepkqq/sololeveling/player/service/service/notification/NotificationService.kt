package com.sleepkqq.sololeveling.player.service.service.notification

import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.service.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.service.lozalization.LocalizationCodes.TASKS_GENERATION_SUCCESS
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
	private val messageSource: MessageSource,
	private val sendNotificationProducer: SendNotificationProducer
) {

	private val log = LoggerFactory.getLogger(javaClass)

	fun send(command: NotificationCommand) {
		val notificationData = when (command) {
			is NotificationCommand.SaveTasks -> createTasksSavedNotification(command.event)
			is NotificationCommand.SilentTasksUpdate -> createTaskCompletedNotification(command.userId)
			is NotificationCommand.UpdateLocale -> createLocaleUpdatedNotification(command.userId)
		}

		sendNotificationSafely(notificationData)
	}

	private fun sendNotificationSafely(notificationData: NotificationData) {
		try {
			sendNotificationProducer.send(notificationData.event)
			log.info(
				"<< {} | txId={}",
				notificationData.successMessage,
				notificationData.event.transactionId
			)
		} catch (e: Exception) {
			log.error(
				"Failed to send {} | txId={}",
				notificationData.errorContext,
				notificationData.event.transactionId,
				e
			)
		}
	}

	private fun createTasksSavedNotification(event: SaveTasksEvent): NotificationData {
		val message = messageSource.getMessage(
			TASKS_GENERATION_SUCCESS,
			null,
			LocaleContextHolder.getLocale()
		)

		val context = NotificationContext(
			txId = event.transactionId,
			userId = event.playerId,
			source = NotificationSource.TASKS,
			message = message,
			visible = true,
			notificationType = "tasks saved"
		)

		return createBaseNotification(context)
	}

	private fun createTaskCompletedNotification(userId: Long): NotificationData {
		val context = NotificationContext(
			txId = UUID.randomUUID().toString(),
			userId = userId,
			source = NotificationSource.TASKS,
			notificationType = "task completed"
		)

		return createBaseNotification(context)
	}

	private fun createLocaleUpdatedNotification(userId: Long): NotificationData {
		val context = NotificationContext(
			txId = UUID.randomUUID().toString(),
			userId = userId,
			source = NotificationSource.LOCALE,
			notificationType = "locale updated"
		)

		return createBaseNotification(context)
	}

	private fun createBaseNotification(context: NotificationContext): NotificationData {
		val notification = Notification(
			context.message,
			context.type,
			context.source,
			context.visible
		)

		val event = SendNotificationEvent(
			context.txId,
			context.userId,
			context.priority,
			notification
		)

		return NotificationData(
			event = event,
			successMessage = "${context.notificationType.replaceFirstChar { it.uppercase() }} notification sent",
			errorContext = "${context.notificationType} notification"
		)
	}

	private data class NotificationData(
		val event: SendNotificationEvent,
		val successMessage: String,
		val errorContext: String
	)

	private data class NotificationContext(
		val txId: String,
		val userId: Long,
		val source: NotificationSource,
		val message: String? = null,
		val visible: Boolean = false,
		val notificationType: String,
		val type: NotificationType = NotificationType.INFO,
		val priority: NotificationPriority = NotificationPriority.LOW
	)
}
