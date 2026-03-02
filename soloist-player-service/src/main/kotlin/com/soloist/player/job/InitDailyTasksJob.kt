package com.soloist.player.job

import com.soloist.player.config.properties.JobsProperties
import com.soloist.player.model.entity.task.enums.DailyTaskType
import com.soloist.player.service.player.DailyTaskService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InitDailyTasksJob(
	properties: JobsProperties,
	private val dailyTaskService: DailyTaskService
) : AbstractStartupJob(
	jobProperties = properties.initDailyTasks
) {

	override val log: Logger = LoggerFactory.getLogger(javaClass)

	override fun runJob() {
		val tasksToInsert = DailyTaskType.entries.flatMap { type ->
			val playerIds = dailyTaskService.findPlayersToInit(type)
			log.info("Found {} players without daily task of type {}", playerIds.size, type)

			playerIds.map { playerId ->
				dailyTaskService.initialize(playerId, type)
			}
		}

		if (tasksToInsert.isEmpty()) {
			log.info("No players found for initialization, exiting job")
			return
		}

		dailyTaskService.insertAll(tasksToInsert)

		val distinctPlayers = tasksToInsert.map { it.player().id() }.toSet().size
		log.info("Initialized {} tasks for {} players", tasksToInsert.size, distinctPlayers)
	}
}
