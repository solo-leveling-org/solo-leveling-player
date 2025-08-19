package com.sleepkqq.sololeveling.player.service.scheduler

import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.service.config.properties.TasksCompletionSchedulerProperties
import com.sleepkqq.sololeveling.player.service.coroutine.TaskGenerationScope
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskStatusService
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Suppress("unused")
@Component
@EnableConfigurationProperties(TasksCompletionSchedulerProperties::class)
class TasksCompletionScheduler(
	private val properties: TasksCompletionSchedulerProperties,
	private val playerTaskService: PlayerTaskService,
	private val playerTaskStatusService: PlayerTaskStatusService,
	private val tasksGenerationScope: TaskGenerationScope
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	@Scheduled(cron = $$"${app.scheduling.tasks.completion.cron}")
	fun call() {

		if (!properties.enabled) {
			log.info("Tasks completion scheduler skipped")
			return
		}

		val playerTasks = playerTaskService.getPendingCompletionTasks()
			.map { PlayerTask(it.toEntity()) { status = PlayerTaskStatus.COMPLETED } }

		log.info("Found {} tasks to complete", playerTasks.size)

		playerTaskStatusService.completeTasks(playerTasks)

		log.info("Tasks updated successfully")

		val tasksByPlayerId = playerTasks.groupBy { it.player.id }

		log.info("Found {} players for tasks generation", tasksByPlayerId.size)

		tasksByPlayerId.forEach { (playerId, tasks) ->
			tasksGenerationScope.scope.launch {
				try {
					playerTaskStatusService.generateTasks(
						playerId = playerId,
						forReplace = true,
						replaceOrders = tasks.map { it.order }.toSet()
					)
					log.info("Successfully generated tasks for player $playerId")
				} catch (e: Exception) {
					log.error("Failed to generate tasks for player $playerId", e)
				}
			}
		}

		log.info("Players tasks generation started asynchronously via coroutines on virtual threads")
	}
}
