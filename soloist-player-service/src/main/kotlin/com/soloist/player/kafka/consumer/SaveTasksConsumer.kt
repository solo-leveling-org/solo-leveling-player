package com.soloist.player.kafka.consumer

import com.soloist.avro.config.consumer.AbstractKafkaConsumer
import com.soloist.avro.constants.KafkaTaskTopics
import com.soloist.avro.idempotency.IdempotencyService
import com.soloist.avro.task.SaveTasksEvent
import com.soloist.player.config.properties.TasksProperties
import com.soloist.player.kafka.producer.TasksSavedProducer
import com.soloist.player.mapper.AvroMapper
import com.soloist.player.model.entity.task.dto.SaveTaskInput
import com.soloist.player.service.player.PlayerTaskService
import com.soloist.player.service.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SaveTasksConsumer(
	private val taskService: TaskService,
	private val playerTaskService: PlayerTaskService,
	private val avroMapper: AvroMapper,
	private val tasksProperties: TasksProperties,
	private val tasksSavedProducer: TasksSavedProducer,
	idempotencyService: IdempotencyService
) : AbstractKafkaConsumer<SaveTasksEvent>(
	idempotencyService = idempotencyService,
	log = LoggerFactory.getLogger(SaveTasksConsumer::class.java)
) {

	@Transactional
	@RetryableTopic
	@KafkaListener(
		topics = [KafkaTaskTopics.SAVE_TASKS_TOPIC],
		groupId = $$"${spring.kafka.avro.group-id}"
	)
	fun listen(event: SaveTasksEvent) {
		consumeWithIdempotency(event)
	}

	override fun getTxId(event: SaveTasksEvent): String = event.txId

	override fun processEvent(event: SaveTasksEvent) {
		val tasks = event.tasks.map(avroMapper::map)
			.onEach {
				it.title.id = UUID.randomUUID()
				it.description.id = UUID.randomUUID()

				if (it.currencyReward == null || it.currencyReward == 0 || it.experience == null || it.experience == 0) {
					val experience = tasksProperties.getExperience(it.rarity)
					val currency = tasksProperties.calculateCurrencyReward(it.rarity)

					it.experience = experience
					it.currencyReward = currency
					log.warn("Applied default rewards for taskId={}", it.id)
				}
			}
			.map(SaveTaskInput::toEntity)

		log.info("Updating {} tasks for player {}", tasks.size, event.userId)
		taskService.updateAll(tasks)

		val taskIds = tasks.map { it.id() }
		val playerTasks = playerTaskService.find(event.userId, taskIds)

		if (playerTasks.isNotEmpty()) {
			log.info(
				"Setting {} player tasks to IN_PROGRESS for player {}",
				playerTasks.size, event.userId
			)
			playerTaskService.inProgressTasks(playerTasks)
		}

		tasksSavedProducer.send(UUID.fromString(event.txId), event.userId, event.operation)
	}
}
