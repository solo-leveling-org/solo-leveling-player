package com.sleepkqq.sololeveling.player.service.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SendNotificationProducer(
	private val kafkaTemplate: KafkaTemplate<String, SendNotificationEvent>
) {

	private val log = LoggerFactory.getLogger(SendNotificationProducer::class.java)

	fun send(event: SendNotificationEvent) {
		kafkaTemplate.send(KafkaTaskTopics.SEND_NOTIFICATION_TOPIC, event)
		log.info(">> Notification sent | transactionId={}", event.transactionId)
	}
}
