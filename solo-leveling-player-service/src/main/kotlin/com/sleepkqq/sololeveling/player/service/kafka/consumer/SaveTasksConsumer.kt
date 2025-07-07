package com.sleepkqq.sololeveling.player.service.kafka.consumer

import com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds
import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerTaskStatus
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.service.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.service.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.service.service.player.PlayerTaskService
import com.sleepkqq.sololeveling.player.service.service.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SaveTasksConsumer(
	private val taskService: TaskService,
	private val playerTaskService: PlayerTaskService,
	private val sendNotificationProducer: SendNotificationProducer,
	private val avroMapper: AvroMapper
) {

	private val log = LoggerFactory.getLogger(SaveTasksConsumer::class.java)

	@KafkaListener(
		topics = [KafkaTaskTopics.SAVE_TASKS_TOPIC],
		groupId = KafkaGroupIds.PLAYER_GROUP_ID,
		containerFactory = "kafkaListenerContainerFactorySaveTasksEvent"
	)
	@Transactional
	fun listen(event: SaveTasksEvent) {
		log.info(">> Start saving tasks | transactionId={}", event.transactionId)
		val now = LocalDateTime.now()
		val tasks = event.tasks.map {
			Task(avroMapper.map(it)) { updatedAt = now }
		}

		taskService.updateTasks(tasks)

		val playerTasksId = playerTaskService.findPlayerTasksId(
			event.playerId,
			tasks.map { it.id }
		)
		playerTaskService.setStatus(playerTasksId, PlayerTaskStatus.IN_PROGRESS, now)

		log.info("<< Tasks successfully saved | transactionId={}", event.transactionId)
		val sendNotificationEvent = SendNotificationEvent(
			event.transactionId,
			event.playerId,
			NotificationPriority.LOW,
			Notification("Your tasks have been successfully generated!")
		)
		sendNotificationProducer.send(sendNotificationEvent)
	}
}
