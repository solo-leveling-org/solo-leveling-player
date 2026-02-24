package com.soloist.player.kafka.producer

import com.soloist.avro.constants.KafkaTaskTopics
import com.soloist.avro.task.GenerateTasksEvent
import com.soloist.avro.task.SaveTasksOperation
import com.soloist.player.mapper.AvroMapper
import com.soloist.player.model.entity.task.dto.GenerateTaskView
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

	fun send(
		txId: UUID = UUID.randomUUID(),
		userId: Long,
		tasks: List<GenerateTaskView>,
		operation: SaveTasksOperation
	) {
		if (tasks.isEmpty()) {
			log.warn("No tasks to generate for userId={}", userId)
		}

		val generateTasks = tasks.map { avroMapper.map(it) }

		val event = GenerateTasksEvent.newBuilder()
			.setTxId(txId.toString())
			.setUserId(userId)
			.setOperation(operation)
			.setTasks(generateTasks)
			.build()

		kafkaTemplate.send(KafkaTaskTopics.GENERATE_TASKS_TOPIC, event.txId, event)
	}
}
