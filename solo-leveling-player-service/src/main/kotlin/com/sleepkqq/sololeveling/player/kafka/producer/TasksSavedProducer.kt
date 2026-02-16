package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.SaveTasksOperation
import com.sleepkqq.sololeveling.avro.task.TasksSavedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TasksSavedProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) {

	fun send(txId: UUID = UUID.randomUUID(), userId: Long, operation: SaveTasksOperation?) {
		val event = TasksSavedEvent(txId.toString(), userId, operation)
		kafkaTemplate.send(KafkaTaskTopics.TASKS_SAVED_TOPIC, event.txId, event)
	}
}