package com.sleepkqq.sololeveling.player.job

import com.sleepkqq.sololeveling.player.config.properties.JobProperties
import com.sleepkqq.sololeveling.player.model.entity.player.sealed.DailyTaskSpec.DailyTaskType
import com.sleepkqq.sololeveling.player.service.player.PlayerDailyTaskService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@EnableConfigurationProperties(JobProperties::class)
class InitDailyTasksJob(
	private val properties: JobProperties,
	private val playerDailyTaskService: PlayerDailyTaskService
) : Ordered {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun getOrder(): Int = properties.initDailyTasks.order

	@Transactional
	@EventListener(ApplicationReadyEvent::class)
	fun call() {
		if (!properties.initDailyTasks.enabled) {
			log.warn("Daily tasks init job is disabled")
			return
		}

		log.info("Starting daily tasks init job")

		val playerIds = playerDailyTaskService.findPlayersToInit()
		log.info("Found {} players without daily tasks", playerIds.size)

		if (playerIds.isEmpty()) {
			log.info("No players found for initialization, exiting job")
			return
		}

		val initializedTasks = playerIds.flatMap { playerId ->
			DailyTaskType.entries.map { type ->
				playerDailyTaskService.initialize(playerId, type)
			}
		}

		playerDailyTaskService.insertAll(initializedTasks)

		log.info("Finished daily tasks init job, initialized {} tasks for {} players",
			initializedTasks.size, playerIds.size)
	}
}
