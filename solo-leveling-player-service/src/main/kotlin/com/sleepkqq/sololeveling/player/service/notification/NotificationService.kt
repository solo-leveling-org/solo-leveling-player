package com.sleepkqq.sololeveling.player.service.notification

import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
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
		val ctx = when (command) {
			is NotificationCommand.SaveTasks -> createTasksSavedNotification(command.userId, command.txId)
			is NotificationCommand.SilentTasksUpdate -> createTaskUpdatedNotification(command.userId)
			is NotificationCommand.UpdateLocale -> createLocaleUpdatedNotification(command.userId)
		}

		val notification = Notification(ctx.message, ctx.type, ctx.source, ctx.visible)
		val event = SendNotificationEvent(ctx.txId, ctx.userId, ctx.priority, notification)

		sendNotificationProducer.send(event)
	}

	private fun createTasksSavedNotification(userId: Long, txId: String): NotificationCtx =
		NotificationCtx(
			txId = txId,
			userId = userId,
			source = NotificationSource.TASKS,
			message = i18nService.getMessage(TASKS_GENERATION_SUCCESS),
			visible = true,
		)

	private fun createTaskUpdatedNotification(userId: Long): NotificationCtx =
		NotificationCtx(
			txId = UUID.randomUUID().toString(),
			userId = userId,
			source = NotificationSource.TASKS,
		)

	private fun createLocaleUpdatedNotification(userId: Long): NotificationCtx =
		NotificationCtx(
			txId = UUID.randomUUID().toString(),
			userId = userId,
			source = NotificationSource.LOCALE,
		)

	private data class NotificationCtx(
		val txId: String,
		val userId: Long,
		val source: NotificationSource,
		val message: String? = null,
		val visible: Boolean = false,
		val type: NotificationType = NotificationType.INFO,
		val priority: NotificationPriority = NotificationPriority.LOW
	)
}
