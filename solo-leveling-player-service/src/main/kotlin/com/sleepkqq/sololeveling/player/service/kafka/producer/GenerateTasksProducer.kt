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
	fun send(playerId: Long) {
		val player = playerService.get(playerId)
		val tasksCount = player.maxTasks - playerTaskService.getCurrentTasksCount(playerId)
		require(!(tasksCount < 1 || tasksCount > player.maxTasks)) {
			"Incorrect current tasks count=$tasksCount, playerId=$playerId"
		}

		val generateTasks = generateSequence { UUID.randomUUID() }
			.take(tasksCount.toInt())
			.onEach { createEmptyPlayerTask(playerId, it) }
			.map { generateTask(player.taskTopics, it) }
			.toList()
		val event = GenerateTasksEvent.newBuilder()
			.setTransactionId(UUID.randomUUID().toString())
			.setPlayerId(playerId)
			.setInputs(generateTasks)
			.build()

		kafkaTemplate.send(KafkaTaskTopics.GENERATE_TASKS_TOPIC, event)
		log.info("<< Generate tasks event sent | transactionId={}", event.transactionId)
	}

	private fun generateTask(playerTaskTopics: List<PlayerTaskTopic>, taskId: UUID): GenerateTask {
		val taskTopicsMap = playerTaskTopics.associateBy { it.taskTopic }
		val topics = defineTaskTopicService.define(taskTopicsMap.keys)
		val mappedTopics = topics.map(taskTopicsMap::getValue)
		return GenerateTask(
			avroMapper.map(taskId),
			avroMapper.map(defineTaskRarityService.define(mappedTopics)),
			topics.map(avroMapper::map)
		)
	}

	private fun createEmptyPlayerTask(linkedPlayerId: Long, taskId: UUID) =
		taskService.insert(
			Task {
				id = taskId
				playerTasks = listOf(
					PlayerTask {
						id = UUID.randomUUID()
						status = PlayerTaskStatus.PREPARING
						playerId = linkedPlayerId
					}
				)
			}
		)
}
