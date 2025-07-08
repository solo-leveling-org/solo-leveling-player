package com.sleepkqq.sololeveling.player.service.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.GenerateTask
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTask
import com.sleepkqq.sololeveling.player.model.entity.player.PlayerTaskTopic
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.service.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerService
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskRarityService
import com.sleepkqq.sololeveling.player.service.service.task.DefineTaskTopicService
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
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
	private val taskService: TaskService,
	private val avroMapper: AvroMapper
) {

	private val log = LoggerFactory.getLogger(GenerateTasksProducer::class.java)

	@Transactional
	@Retryable(
		value = [Exception::class],
		maxAttempts = 3,
		backoff = Backoff(delay = 1000, multiplier = 2.0)
	)
	fun send(playerId: Long) {
		log.info(">> Start generating tasks for player {}", playerId)
		
		try {
			// Validate player exists
			val player = playerService.get(playerId)
			val tasksCount = player.maxTasks - playerTaskService.getCurrentTasksCount(playerId)
			
			require(tasksCount >= 1 && tasksCount <= player.maxTasks) {
				"Incorrect current tasks count=$tasksCount, playerId=$playerId, maxTasks=${player.maxTasks}"
			}

			// Generate task IDs
			val taskIds = generateSequence { UUID.randomUUID() }
				.take(tasksCount.toInt())
				.toList()

			log.debug("Generated {} task IDs for player {}", taskIds.size, playerId)

			// Insert empty tasks
			taskService.insertTasks(taskIds.map { emptyTask(it, playerId) })

			// Create generate tasks event
			val generateTasks = taskIds.map { generateTask(player.taskTopics, it) }
			val event = GenerateTasksEvent.newBuilder()
				.setTransactionId(UUID.randomUUID().toString())
				.setPlayerId(playerId)
				.setInputs(generateTasks)
				.build()

			// Send to Kafka with retry and error handling
			sendWithRetry(event)
			
		} catch (e: Exception) {
			log.error("Failed to generate tasks for player {}: {}", playerId, e.message, e)
			throw e
		}
	}

	private fun sendWithRetry(event: GenerateTasksEvent) {
		try {
			kafkaTemplate.send(KafkaTaskTopics.GENERATE_TASKS_TOPIC, event.transactionId, event)
			log.info("<< Generate tasks event sent successfully | transactionId={}", event.transactionId)
		} catch (throwable: Exception) {
			log.error("Failed to send generate tasks event | transactionId={}, error={}",
				event.transactionId, throwable.message, throwable)
			throw RuntimeException("Failed to send generate tasks event", throwable)
		}
	}

	private fun generateTask(playerTaskTopics: List<PlayerTaskTopic>, taskId: UUID): GenerateTask {
		try {
			val taskTopicsMap = playerTaskTopics.associateBy { it.taskTopic }
			val topics = defineTaskTopicService.define(taskTopicsMap.keys)
			val mappedTopics = topics.map(taskTopicsMap::getValue)
			
			return GenerateTask(
				avroMapper.map(taskId),
				avroMapper.map(defineTaskRarityService.define(mappedTopics)),
				topics.map(avroMapper::map)
			)
		} catch (e: Exception) {
			log.error("Failed to generate task for taskId {}: {}", taskId, e.message, e)
			throw e
		}
	}

	private fun emptyTask(taskId: UUID, linkedPlayerId: Long): Task = Task {
		id = taskId
		playerTasks = listOf(
			PlayerTask {
				id = UUID.randomUUID()
				status = PlayerTaskStatus.PREPARING
				playerId = linkedPlayerId
			}
		)
	}
}
