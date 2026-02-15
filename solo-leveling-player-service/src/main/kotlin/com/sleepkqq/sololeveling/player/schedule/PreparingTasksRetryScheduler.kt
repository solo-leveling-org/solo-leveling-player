package com.sleepkqq.sololeveling.player.schedule

import com.sleepkqq.sololeveling.avro.task.SaveTasksOperation
import com.sleepkqq.sololeveling.player.kafka.producer.GenerateTasksProducer
import com.sleepkqq.sololeveling.player.model.entity.task.dto.GenerateTaskView
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PreparingTasksRetryScheduler(
	@Value($$"${app.scheduler.preparing-tasks-retry.enabled}")
	private val enabled: Boolean,
	private val playerTaskService: PlayerTaskService,
	private val generateTasksProducer: GenerateTasksProducer
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional(readOnly = true)
	@Scheduled(cron = $$"${app.scheduler.preparing-tasks-retry.cron}", zone = "UTC")
	fun call() {
		if (!enabled) {
			log.warn("Preparing tasks retry scheduler is disabled")
			return
		}

		log.info("Starting preparing tasks retry scheduler")

		val preparingTasks = playerTaskService.getPreparingTasksForRetry()
		log.info("Fetched {} preparing tasks for retry", preparingTasks.size)

		if (preparingTasks.isEmpty()) {
			log.info("No preparing tasks found, exiting scheduler")
			return
		}

		preparingTasks
			.groupBy(
				{ it.player.id },
				{ it.task }
			)
			.forEach { (playerId, tasks) ->
				log.info("Generating tasks for playerId={} with tasks={}", playerId, tasks.map { it.id })
				generateTasksProducer.send(
					userId = playerId,
					tasks = tasks.map { GenerateTaskView(it.toEntity()) },
					operation = SaveTasksOperation.COMPLETE
				)
			}

		log.info("Finished preparing tasks retry scheduler")
	}
}