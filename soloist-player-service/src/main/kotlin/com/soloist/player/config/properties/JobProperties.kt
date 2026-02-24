package com.soloist.player.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.job")
data class JobProperties(
	val initDailyTasks: InitDailyTasksProperties
) {

	data class InitDailyTasksProperties(
		val enabled: Boolean,
		val order: Int
	)
}
