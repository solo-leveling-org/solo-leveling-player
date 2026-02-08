package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SendNotificationProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) {

	fun send(event: SendNotificationEvent) =
		kafkaTemplate.send(KafkaTaskTopics.SEND_NOTIFICATION_TOPIC, event.txId, event)
}
