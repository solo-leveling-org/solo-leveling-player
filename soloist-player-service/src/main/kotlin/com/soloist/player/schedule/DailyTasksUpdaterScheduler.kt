package com.soloist.player.schedule

import com.soloist.player.service.player.DayStreakService
import com.soloist.player.service.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DailyTasksUpdaterScheduler(
	@Value("\${app.scheduler.daily-tasks-updater.enabled}")
	private val enabled: Boolean,
	private val dayStreakService: DayStreakService,
	private val taskService: TaskService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@Scheduled(cron = "\${app.scheduler.daily-tasks-updater.cron}", zone = "UTC")
	fun call() {
		if (!enabled) {
			log.warn("Daily tasks updater scheduler is disabled")
			return
		}

		log.info("Starting daily tasks updater scheduler")

		val resetCount = dayStreakService.resetExpiredStreaks()
		log.info("Reset {} expired day streaks", resetCount)

		val initializedCount = taskService.initializeDailyTasksForAllPlayers()
		log.info("Initialized daily tasks for {} players", initializedCount)

		log.info("Finished daily tasks updater scheduler")
	}
}
