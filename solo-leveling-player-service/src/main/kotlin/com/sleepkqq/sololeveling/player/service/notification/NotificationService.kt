package com.sleepkqq.sololeveling.player.service.notification

import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.player.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCode
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCode.TASKS_GENERATION_SUCCESS
import com.sleepkqq.sololeveling.player.service.i18n.I18nService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
	private val i18nService: I18nService,
	private val sendNotificationProducer: SendNotificationProducer
) {

	fun send(command: NotificationCommand) {
		val ctx = command.toContext()

		val message = ctx.localizationCode?.let { i18nService.getMessage(it) }
		val notification = Notification(message, ctx.type, ctx.source, ctx.visible)
		val event = SendNotificationEvent(ctx.txId, ctx.userId, ctx.priority, notification)

		sendNotificationProducer.send(event)
	}

	data class NotificationCtx(
		val txId: String,
		val userId: Long,
		val source: NotificationSource,
		val localizationCode: LocalizationCode? = null,
		val visible: Boolean = false,
		val type: NotificationType = NotificationType.INFO,
		val priority: NotificationPriority = NotificationPriority.LOW
	)

	sealed interface NotificationCommand {
		val userId: Long
		val txId: String get() = UUID.randomUUID().toString()

		fun toContext(): NotificationCtx

		data class SaveTasks(
			override val userId: Long,
			override val txId: String = UUID.randomUUID().toString()
		) : NotificationCommand {
			override fun toContext() = NotificationCtx(
				txId = txId,
				userId = userId,
				source = NotificationSource.TASKS,
				localizationCode = TASKS_GENERATION_SUCCESS,
				visible = true
			)
		}

		data class SilentTasksUpdate(override val userId: Long) : NotificationCommand {
			override fun toContext() = NotificationCtx(
				txId = txId,
				userId = userId,
				source = NotificationSource.TASKS
			)
		}

		data class UpdateLocale(override val userId: Long) : NotificationCommand {
			override fun toContext() = NotificationCtx(
				txId = txId,
				userId = userId,
				source = NotificationSource.LOCALE
			)
		}
	}
}
