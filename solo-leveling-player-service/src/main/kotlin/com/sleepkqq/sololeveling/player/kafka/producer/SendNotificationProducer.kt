package com.sleepkqq.sololeveling.player.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.player.service.notification.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SendNotificationProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) {

	private val log = LoggerFactory.getLogger(javaClass)

	fun send(data: NotificationService.NotificationData) {
		val event = data.event
		kafkaTemplate.send(KafkaTaskTopics.SEND_NOTIFICATION_TOPIC, event.txId, event)
			.whenComplete { _, e ->
				if (e == null) {
					log.info(
						"<< {} notification event sent | txId={}",
						data.message.replaceFirstChar { it.uppercase() },
						event.txId
					)
				} else {
					log.error("Failed to send {} notification event | txId={}", data.message, event.txId, e)
				}
			}
	}
}
