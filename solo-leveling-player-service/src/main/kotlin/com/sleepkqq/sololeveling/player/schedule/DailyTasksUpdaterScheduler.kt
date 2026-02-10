package com.sleepkqq.sololeveling.player.schedule

import com.sleepkqq.sololeveling.player.model.entity.player.dto.ReplacePlayerDailyTaskView
import com.sleepkqq.sololeveling.player.service.player.PlayerDailyTaskService
import com.sleepkqq.sololeveling.player.service.player.PlayerDayStreakService
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

		val tasks = playerDailyTaskService.findView(ReplacePlayerDailyTaskView::class)
		log.info("Fetched {} daily tasks for update", tasks.size)

		if (tasks.isEmpty()) {
			log.info("No daily tasks found, exiting scheduler")
			return
		}

		val updatedTasks = tasks.map { playerDailyTaskService.replace(it.toEntity()) }
		playerDailyTaskService.updateAll(updatedTasks)

		log.info("Finished daily tasks updater scheduler, updated {} tasks", updatedTasks.size)
	}
}
