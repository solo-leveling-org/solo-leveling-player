package com.sleepkqq.sololeveling.player.event.model

import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCode
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCode.TASKS_GENERATION_SUCCESS
import java.util.UUID

data class NotificationCtx(
	val txId: String,
	val userId: Long,
	val source: NotificationSource,
	val localizationCode: LocalizationCode? = null,
	val visible: Boolean = false,
	val type: NotificationType = NotificationType.INFO,
	val priority: NotificationPriority = NotificationPriority.LOW
)

sealed interface NotificationEvent {
	val userId: Long
	val txId: String get() = UUID.randomUUID().toString()

	fun toContext(): NotificationCtx
}

data class TasksSavedEvent(
	override val userId: Long,
	override val txId: String = UUID.randomUUID().toString()
) : NotificationEvent {
	override fun toContext() = NotificationCtx(
		txId = txId,
		userId = userId,
		source = NotificationSource.TASKS,
		localizationCode = TASKS_GENERATION_SUCCESS,
		visible = true
	)
}

data class TasksSilentUpdatedEvent(override val userId: Long) : NotificationEvent {
	override fun toContext() = NotificationCtx(
		txId = txId,
		userId = userId,
		source = NotificationSource.TASKS
	)
}

data class LocaleUpdatedEvent(override val userId: Long) : NotificationEvent {
	override fun toContext() = NotificationCtx(
		txId = txId,
		userId = userId,
		source = NotificationSource.LOCALE
	)
}
