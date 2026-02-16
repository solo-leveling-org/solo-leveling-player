package com.sleepkqq.sololeveling.player.job

import com.sleepkqq.sololeveling.player.config.properties.JobProperties
import com.sleepkqq.sololeveling.player.model.entity.player.enums.DailyTaskType
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

		val tasksToInsert = DailyTaskType.entries.flatMap { type ->
			val playerIds = playerDailyTaskService.findPlayersToInit(type)
			log.info(
				"Found {} players without daily task of type {}",
				playerIds.size, type
			)

			playerIds.map { playerId ->
				playerDailyTaskService.initialize(playerId, type)
			}
		}

		if (tasksToInsert.isEmpty()) {
			log.info("No players found for initialization, exiting job")
			return
		}

		playerDailyTaskService.insertAll(tasksToInsert)

		val distinctPlayers = tasksToInsert.map { it.player().id() }.toSet().size
		log.info(
			"Finished daily tasks init job, initialized {} tasks for {} players",
			tasksToInsert.size, distinctPlayers
		)
	}
}
