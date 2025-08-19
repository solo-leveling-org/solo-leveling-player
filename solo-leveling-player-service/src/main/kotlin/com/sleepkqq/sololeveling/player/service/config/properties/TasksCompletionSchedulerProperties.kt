package com.sleepkqq.sololeveling.player.service.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.scheduling.tasks.completion")
data class TasksCompletionSchedulerProperties(
	val enabled: Boolean,
	val cron: String
)
