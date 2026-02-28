package com.soloist.player.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "app.job")
data class JobsProperties(
	val initDailyTasks: JobProperties,
	val initVectorTasks: JobProperties
) {

	data class JobProperties(
		val enabled: Boolean,
		val order: Int,
		val pageSize: Int? = null,
		val delay: Duration? = null
	)
}
