package com.sleepkqq.sololeveling.player.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.scheduler.leaderboard-snapshot")
data class LeaderboardSnapshotSchedulerProperties(
	val enabled: Boolean,
	val cron: String
)
