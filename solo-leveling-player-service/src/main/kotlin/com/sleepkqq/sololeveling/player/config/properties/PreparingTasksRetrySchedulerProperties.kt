package com.sleepkqq.sololeveling.player.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.scheduler.preparing-tasks-retry")
data class PreparingTasksRetrySchedulerProperties(
	val enabled: Boolean,
	val cron: String
)
