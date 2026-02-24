package com.soloist.player.kafka.producer

import com.soloist.avro.constants.KafkaTaskTopics
import com.soloist.avro.user.LocaleUpdatedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LocaleUpdatedProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) {

	fun send(txId: UUID = UUID.randomUUID(), userId: Long) {
		val event = LocaleUpdatedEvent(txId.toString(), userId)
		kafkaTemplate.send(KafkaTaskTopics.LOCALE_UPDATED_TOPIC, event.txId, event)
	}
}