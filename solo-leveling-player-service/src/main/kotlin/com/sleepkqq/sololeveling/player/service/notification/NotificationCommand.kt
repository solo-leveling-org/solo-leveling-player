package com.sleepkqq.sololeveling.player.service.notification

import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent

sealed class NotificationCommand {
	data class SaveTasks(val event: SaveTasksEvent) : NotificationCommand()
	data class SilentTasksUpdate(val userId: Long) : NotificationCommand()
	data class UpdateLocale(val userId: Long) : NotificationCommand()
}
