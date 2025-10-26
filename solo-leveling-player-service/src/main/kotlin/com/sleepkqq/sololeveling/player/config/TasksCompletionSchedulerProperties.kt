package com.sleepkqq.sololeveling.player.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.scheduling.tasks.completion")
data class TasksCompletionSchedulerProperties(
	val enabled: Boolean,
	val cron: String
)