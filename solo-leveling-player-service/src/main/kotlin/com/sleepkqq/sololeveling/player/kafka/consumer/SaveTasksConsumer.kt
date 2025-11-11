package com.sleepkqq.sololeveling.player.kafka.consumer

import com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds
import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.model.entity.task.dto.TaskInput
import com.sleepkqq.sololeveling.player.service.notification.NotificationCommand
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import com.sleepkqq.sololeveling.player.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.redis.IdempotencyService
import com.sleepkqq.sololeveling.player.service.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Suppress("unused")
@Service
class SaveTasksConsumer(
	private val taskService: TaskService,
	private val playerTaskService: PlayerTaskService,
	private val avroMapper: AvroMapper,
	private val idempotencyService: IdempotencyService,
	private val notificationService: NotificationService
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@KafkaListener(
		topics = [KafkaTaskTopics.SAVE_TASKS_TOPIC],
		groupId = KafkaGroupIds.PLAYER_GROUP_ID,
		containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
	)
	@Transactional
	fun listen(event: SaveTasksEvent, ack: Acknowledgment) {
		val txId = event.txId

		log.info(">> Start processing SaveTasksEvent | txId={}", txId)

		try {
			if (idempotencyService.isProcessed(txId)) {
				ack.acknowledge()
				return
			}

			processSaveTasksEvent(event)

			notificationService.send(NotificationCommand.SaveTasks(event))

			log.info("Successfully processed SaveTasksEvent | txId={}", txId)

			ack.acknowledge()

		} catch (e: Exception) {
			log.error("Failed to process SaveTasksEvent | txId={}", txId, e)
			throw e
		}
	}

	private fun processSaveTasksEvent(event: SaveTasksEvent) {

		val tasks = event.tasks.map(avroMapper::map)
			.onEach {
				it.title!!.id = UUID.randomUUID()
				it.description!!.id = UUID.randomUUID()
			}
			.map(TaskInput::toEntity)

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
	}
}
