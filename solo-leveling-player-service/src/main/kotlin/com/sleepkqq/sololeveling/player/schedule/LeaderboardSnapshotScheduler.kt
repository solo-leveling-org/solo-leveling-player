package com.sleepkqq.sololeveling.player.schedule

import com.sleepkqq.sololeveling.player.config.properties.LeaderboardSnapshotSchedulerProperties
import com.sleepkqq.sololeveling.player.service.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@EnableConfigurationProperties(LeaderboardSnapshotSchedulerProperties::class)
class LeaderboardSnapshotScheduler(
	private val leaderboardSnapshotSchedulerProperties: LeaderboardSnapshotSchedulerProperties,
	private val userService: UserService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional(readOnly = true)
	@Scheduled(cron = $$"${app.scheduler.leaderboard-snapshot.cron}", zone = "UTC")
	fun call() {
		if (!leaderboardSnapshotSchedulerProperties.enabled) {
			log.warn("Leaderboard snapshot scheduler is disabled")
			return
		}


	}
}