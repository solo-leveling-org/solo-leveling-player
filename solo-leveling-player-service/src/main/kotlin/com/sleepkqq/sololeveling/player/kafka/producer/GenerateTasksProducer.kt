package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class GenerateTasksProducer(
	private val kafkaTemplate: KafkaTemplate<String, GenerateTasksEvent>
) {

	private val log = LoggerFactory.getLogger(javaClass)

	fun send(event: GenerateTasksEvent) {
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
