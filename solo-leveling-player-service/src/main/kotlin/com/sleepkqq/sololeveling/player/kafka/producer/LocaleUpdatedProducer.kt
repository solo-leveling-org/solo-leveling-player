package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.user.LocaleUpdatedEvent
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