package com.sleepkqq.sololeveling.player.schedule

import com.sleepkqq.sololeveling.player.model.entity.player.dto.ReplacePlayerDailyTaskView
import com.sleepkqq.sololeveling.player.service.player.PlayerDailyTaskService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyTasksUpdaterScheduler(
	@Value($$"${app.scheduler.daily-tasks-updater.enabled}")
	private val enabled: Boolean,
	private val playerDailyTaskService: PlayerDailyTaskService
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

		val tasks = playerDailyTaskService.findView(ReplacePlayerDailyTaskView::class)

		val updatedTasks = tasks.map { playerDailyTaskService.replace(it.toEntity()) }

		playerDailyTaskService.updateAll(updatedTasks)
	}
}
