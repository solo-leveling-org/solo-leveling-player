package com.sleepkqq.sololeveling.player.service.notification

import java.util.UUID

sealed class NotificationCommand {
	data class SaveTasks(
		val userId: Long,
		val txId: String = UUID.randomUUID().toString()
	) : NotificationCommand()
	data class SilentTasksUpdate(val userId: Long) : NotificationCommand()
	data class UpdateLocale(val userId: Long) : NotificationCommand()
}
