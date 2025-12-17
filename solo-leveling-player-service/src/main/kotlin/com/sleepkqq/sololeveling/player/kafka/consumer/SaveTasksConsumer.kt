package com.sleepkqq.sololeveling.player.kafka.consumer

import com.sleepkqq.sololeveling.avro.config.consumer.AbstractKafkaConsumer
import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.idempotency.IdempotencyService
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.config.properties.TasksProperties
import com.sleepkqq.sololeveling.player.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.model.entity.task.dto.SaveTaskInput
import com.sleepkqq.sololeveling.player.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.task.TaskService
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
	private val notificationService: NotificationService,
	private val tasksProperties: TasksProperties,
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
				it.title!!.id = UUID.randomUUID()
				it.description!!.id = UUID.randomUUID()

				if (it.currencyReward == null || it.currencyReward == 0 || it.experience == null || it.experience == 0) {
					val experience = tasksProperties.getExperience(it.rarity)
					val currency = tasksProperties.calculateCurrencyReward(it.rarity)

					it.experience = experience
					it.currencyReward = currency
					log.warn("Applied default rewards for taskId={}", it.id)
				}
			}
			.map(SaveTaskInput::toEntity)

		log.info("Updating {} tasks for player {}", tasks.size, event.playerId)
		taskService.updateAll(tasks)

		val taskIds = tasks.map { it.id() }
		val playerTasks = playerTaskService.find(event.playerId, taskIds)

		if (playerTasks.isNotEmpty()) {
			log.info(
				"Setting {} player tasks to IN_PROGRESS for player {}",
				playerTasks.size, event.playerId
			)
			playerTaskService.inProgressTasks(playerTasks)
		}

		notificationService.send(NotificationCommand.SaveTasks(event.playerId, event.txId))
	}
}
