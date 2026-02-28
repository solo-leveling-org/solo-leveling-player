package com.soloist.player.job

import com.soloist.player.config.properties.JobsProperties
import com.soloist.player.model.entity.player.enums.DailyTaskType
import com.soloist.player.service.player.PlayerDailyTaskService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InitDailyTasksJob(
	properties: JobsProperties,
	private val playerDailyTaskService: PlayerDailyTaskService
) : AbstractStartupJob(
	jobName = "init-daily-tasks-job",
	jobProperties = properties.initDailyTasks
) {

	override val log = LoggerFactory.getLogger(javaClass)

	override fun runJob() {
		val tasksToInsert = DailyTaskType.entries.flatMap { type ->
			val playerIds = playerDailyTaskService.findPlayersToInit(type)
			log.info("Found {} players without daily task of type {}", playerIds.size, type)

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
		log.info("Initialized {} tasks for {} players", tasksToInsert.size, distinctPlayers)
	}
}
