package com.soloist.player.kafka.producer

import com.soloist.avro.constants.KafkaTaskTopics
import com.soloist.avro.player.DayStreakExtendedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DayStreakExtendedProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) {

	fun send(txId: UUID = UUID.randomUUID(), userId: Long) {
		val event = DayStreakExtendedEvent(txId.toString(), userId)
		kafkaTemplate.send(KafkaTaskTopics.DAY_STREAK_EXTENDED_TOPIC, event.txId, event)
	}
}