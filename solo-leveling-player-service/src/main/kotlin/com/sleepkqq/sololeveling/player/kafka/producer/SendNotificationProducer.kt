package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SendNotificationProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) {

	fun send(data: NotificationService.NotificationData) {
		val event = data.event
		kafkaTemplate.send(KafkaTaskTopics.SEND_NOTIFICATION_TOPIC, event.txId, event)
	}
}
