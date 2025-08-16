package com.sleepkqq.sololeveling.player.service.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.service.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskRarityService
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskTopicService
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
	fun send(playerId: Long, forReplace: Boolean = false, replaceOrders: Set<Int> = setOf()) {

		log.info(">> Start generating tasks for player {}", playerId)

		try {
			val player = playerService.get(playerId) {
				maxTasks()
				taskTopics {
					taskTopic()
					level { level() }
				}
			}

			val playerTasks = if (forReplace) {
				replaceOrders.map { playerTaskService.initialize(playerId, it) }

			} else {
				val tasksToGenerateCount = player.maxTasks - playerTaskService.getActiveTasksCount(playerId)

				require(tasksToGenerateCount >= 1 && tasksToGenerateCount <= player.maxTasks) {
					"Incorrect tasks to generate count=$tasksToGenerateCount, playerId=$playerId, maxTasks=${player.maxTasks}"
				}

				(0 until tasksToGenerateCount.toInt())
					.map { index -> playerTaskService.initialize(playerId, index) }
					.toList()
			}

			log.info("Generated {} tasks for player {}", playerTasks.size, playerId)

			playerTaskService.insertAll(playerTasks)

			val generateTasks = playerTasks.map {
				generateTask(it.task, player.taskTopics)
			}

			val event = GenerateTasksEvent.newBuilder()
				.setTransactionId(UUID.randomUUID().toString())
				.setPlayerId(playerId)
				.setInputs(generateTasks)
				.build()

			kafkaTemplate.send(KafkaTaskTopics.GENERATE_TASKS_TOPIC, event.transactionId, event)

		} catch (e: Exception) {
			log.error("Failed to generate tasks for player {}: {}", playerId, e.message, e)
			throw e
		}
	}

	private fun generateTask(task: Task, playerTaskTopics: List<PlayerTaskTopic>): GenerateTask {
		val playerTaskTopicsMap = playerTaskTopics
			.filter { it.isActive }
			.associateBy { it.taskTopic }

		val definedTopics = defineTaskTopicService.define(playerTaskTopicsMap.keys)
		val chosenTopics = definedTopics.map(playerTaskTopicsMap::getValue)

		val rarity = defineTaskRarityService.define(chosenTopics)

		return avroMapper.map(task, definedTopics, rarity)
	}
}
