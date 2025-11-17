package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.player.mapper.AvroMapper
import com.sleepkqq.sololeveling.player.model.entity.task.Task
import com.sleepkqq.sololeveling.player.model.entity.task.dto.GenerateTaskView
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GenerateTasksProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>,
	private val avroMapper: AvroMapper
) {

	private val log = LoggerFactory.getLogger(javaClass)

	fun send(playerId: Long, tasks: List<Task>) {

		val generateTasks = tasks.map { avroMapper.map(GenerateTaskView(it)) }

		val event = GenerateTasksEvent.newBuilder()
			.setTxId(UUID.randomUUID().toString())
			.setPlayerId(playerId)
			.setInputs(generateTasks)
			.build()

		kafkaTemplate.send(KafkaTaskTopics.GENERATE_TASKS_TOPIC, event.txId, event)
			.whenComplete { _, e ->
				if (e == null) {
					log.info("<< Generate tasks event sent | txId={}", event.txId)
				} else {
					log.error("Failed to send generate tasks event | txId={}", event.txId, e)
				}
			}
	}
}
