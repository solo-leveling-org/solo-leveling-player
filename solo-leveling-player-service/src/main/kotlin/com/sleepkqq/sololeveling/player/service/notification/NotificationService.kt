package com.sleepkqq.sololeveling.player.service.notification

import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCodes.TASKS_GENERATION_SUCCESS
import com.sleepkqq.sololeveling.player.service.i18n.I18nService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
	private val i18nService: I18nService,
	private val sendNotificationProducer: SendNotificationProducer
) {

	fun send(command: NotificationCommand) {
		val notificationData = when (command) {
			is NotificationCommand.SaveTasks -> createTasksSavedNotification(command.event)
			is NotificationCommand.SilentTasksUpdate -> createTaskUpdatedNotification(command.userId)
			is NotificationCommand.UpdateLocale -> createLocaleUpdatedNotification(command.userId)
		}

		sendNotificationProducer.send(notificationData)
	}

	private fun createTasksSavedNotification(event: SaveTasksEvent): NotificationData {
		val message = i18nService.getMessage(TASKS_GENERATION_SUCCESS)

		val context = NotificationCtx(
			txId = event.txId,
			userId = event.playerId,
			source = NotificationSource.TASKS,
			message = message,
			visible = true,
			notificationCause = "tasks saved"
		)

		return createBaseNotification(context)
	}

	private fun createTaskUpdatedNotification(userId: Long): NotificationData {
		val context = NotificationCtx(
			txId = UUID.randomUUID().toString(),
			userId = userId,
			source = NotificationSource.TASKS,
			notificationCause = "task updated"
		)

		return createBaseNotification(context)
	}

	private fun createLocaleUpdatedNotification(userId: Long): NotificationData {
		val context = NotificationCtx(
			txId = UUID.randomUUID().toString(),
			userId = userId,
			source = NotificationSource.LOCALE,
			notificationCause = "locale updated"
		)

		return createBaseNotification(context)
	}

	private fun createBaseNotification(context: NotificationCtx): NotificationData {
		val notification = Notification(context.message, context.type, context.source, context.visible)

		val event = SendNotificationEvent(context.txId, context.userId, context.priority, notification)

		return NotificationData(event, context.notificationCause)
	}

	private data class NotificationCtx(
		val txId: String,
		val userId: Long,
		val source: NotificationSource,
		val message: String? = null,
		val visible: Boolean = false,
		val notificationCause: String,
		val type: NotificationType = NotificationType.INFO,
		val priority: NotificationPriority = NotificationPriority.LOW
	)

	data class NotificationData(
		val event: SendNotificationEvent,
		val message: String
	)
}
