package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.player.model.entity.Fetchers
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.task.DefineTaskRarityService
import com.sleepkqq.sololeveling.player.service.task.DefineTaskTopicService
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class GenerateTasksProducer(
	private val kafkaTemplate: KafkaTemplate<String, GenerateTasksEvent>,
	private val defineTaskTopicService: DefineTaskTopicService,
	private val defineTaskRarityService: DefineTaskRarityService,
	private val playerService: PlayerService,
	private val playerTaskService: PlayerTaskService,
	private val avroMapper: AvroMapper
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@Transactional
	@Retryable(maxAttempts = 3, backoff = Backoff(delay = 1000, multiplier = 2.0))
	fun send(playerId: Long, replaceOrders: Set<Int> = setOf()) {

		log.info(">> Start generating tasks for player {}", playerId)

		try {
			val player = playerService.get(
				playerId,
				Fetchers.PLAYER_FETCHER.maxTasks()
					.taskTopics(
						Fetchers.PLAYER_TASK_TOPIC_FETCHER
							.taskTopic()
							.active()
							.level(
								Fetchers.LEVEL_FETCHER
									.level()
							)
					)
					.tasks(
						Fetchers.PLAYER_TASK_FETCHER
							.order()
					)
			)

			val activeTasksCount = playerTaskService.getActiveTasksCount(playerId)
			val maxTasks = player.maxTasks()

			// Генерируем задачи для замены (если указаны)
			val replaceTasks = replaceOrders.map {
				playerTaskService.initialize(playerId, it)
			}

			// Вычисляем сколько еще нужно задач для достижения maxTasks
			val additionalTasksNeeded = maxTasks - activeTasksCount

			if (additionalTasksNeeded < 0) {
				log.warn(
					"Invalid state: tasksAfterReplace={} exceeds maxTasks={} for playerId={}, skipping generation",
					activeTasksCount, maxTasks, playerId
				)
				return
			}

			val usedOrders = player.tasks()
				.map { it.order() }

			// Генерируем дополнительные задачи для заполнения до maxTasks
			val additionalTasks = if (additionalTasksNeeded > 0) {
				(0 until maxTasks)
					.filterNot { it in usedOrders }
					.take(additionalTasksNeeded.toInt())
					.map { playerTaskService.initialize(playerId, it) }
			} else {
				emptyList()
			}

			val allNewTasks = replaceTasks + additionalTasks

			if (allNewTasks.isEmpty()) {
				log.warn("No tasks generated for playerId={}, skipping", playerId)
				return
			}

			log.info(
				"Generated {} tasks for player {} ({} replace, {} new)",
				allNewTasks.size, playerId, replaceTasks.size, additionalTasks.size
			)

			playerTaskService.insertAll(allNewTasks)

			val generateTasks = allNewTasks.map {
				generateTask(it.task(), player.taskTopics())
			}

			val event = GenerateTasksEvent.newBuilder()
				.setTxId(UUID.randomUUID().toString())
				.setPlayerId(playerId)
				.setInputs(generateTasks)
				.build()

			kafkaTemplate.send(KafkaTaskTopics.GENERATE_TASKS_TOPIC, event.txId, event)

		} catch (e: Exception) {
			log.error("Failed to generate tasks for player {}", playerId, e)
			return
		}
	}

	private fun generateTask(task: Task, playerTaskTopics: List<PlayerTaskTopic>): GenerateTask {
		val playerTaskTopicsMap = playerTaskTopics
			.filter { it.isActive() }
			.associateBy { it.taskTopic() }

		val definedTopics = defineTaskTopicService.define(playerTaskTopicsMap.keys)
		val chosenTopics = definedTopics.map(playerTaskTopicsMap::getValue)

		val rarity = defineTaskRarityService.define(chosenTopics)

		return avroMapper.map(task, definedTopics, rarity)
	}
}
