package com.soloist.player.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.job")
data class JobsProperties(
	val initDailyTasks: JobProperties,
	val initVectorTasks: JobProperties
) {

	data class JobProperties(
		val enabled: Boolean,
		val order: Int
	)
}
