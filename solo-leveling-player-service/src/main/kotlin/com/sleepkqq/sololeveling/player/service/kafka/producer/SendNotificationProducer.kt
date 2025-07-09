package com.sleepkqq.sololeveling.player.service.kafka.producer

import com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class SendNotificationProducer(
	private val kafkaTemplate: KafkaTemplate<String, SendNotificationEvent>
) {

	private val log = LoggerFactory.getLogger(SendNotificationProducer::class.java)

	@Retryable(maxAttempts = 3, backoff = Backoff(delay = 1000, multiplier = 2.0))
	fun send(event: SendNotificationEvent) {
		log.info(
			">> Sending notification event | transactionId={}, priority={}",
			event.transactionId, event.priority
		)

		try {
			kafkaTemplate.send(KafkaTaskTopics.SEND_NOTIFICATION_TOPIC, event.transactionId, event)
			log.info("<< Notification event sent successfully | transactionId={}", event.transactionId)
		} catch (e: Exception) {
			log.error(
				"Failed to send notification event | transactionId={}, error={}",
				event.transactionId, e.message, e
			)
			throw RuntimeException("Failed to send notification event", e)
		}
	}
}
