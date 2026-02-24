package com.soloist.player.schedule

import com.soloist.player.model.entity.player.enums.DailyTaskType
import com.soloist.player.service.player.PlayerDailyTaskService
import com.soloist.player.service.player.PlayerDayStreakService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyTasksUpdaterScheduler(
	@Value($$"${app.scheduler.daily-tasks-updater.enabled}")
	private val enabled: Boolean,
	private val playerDailyTaskService: PlayerDailyTaskService,
	private val playerDayStreakService: PlayerDayStreakService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	@Scheduled(cron = $$"${app.scheduler.daily-tasks-updater.cron}", zone = "UTC")
	fun call() {
		if (!enabled) {
			log.warn("Daily tasks updater scheduler is disabled")
			return
		}

		log.info("Starting daily tasks updater scheduler")

		val resetCount = playerDayStreakService.resetExpiredStreaks()
		log.info("Reset {} expired day streaks", resetCount)

		DailyTaskType.entries.forEach {
			val updatedTasksCount = playerDailyTaskService.replace(it)
			log.info("Updated {} tasks by type={}", updatedTasksCount, it)
		}

		log.info("Finished daily tasks updater scheduler")
	}
}
