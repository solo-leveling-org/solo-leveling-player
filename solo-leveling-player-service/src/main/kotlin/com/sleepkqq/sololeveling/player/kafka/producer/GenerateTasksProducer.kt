package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.player.mapper.AvroMapper
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

	fun send(txId: UUID = UUID.randomUUID(), userId: Long, tasks: List<GenerateTaskView>) {
		if (tasks.isEmpty()) {
			log.warn("No tasks to generate for userId={}", userId)
		}

		val generateTasks = tasks.map { avroMapper.map(it) }

		val event = GenerateTasksEvent.newBuilder()
			.setTxId(txId.toString())
			.setUserId(userId)
			.setTasks(generateTasks)
			.build()

		kafkaTemplate.send(KafkaTaskTopics.GENERATE_TASKS_TOPIC, event.txId, event)
	}
}
