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

	private val log = LoggerFactory.getLogger(javaClass)

	@Retryable(maxAttempts = 3, backoff = Backoff(delay = 1000, multiplier = 2.0))
	fun send(event: SendNotificationEvent) {
		val txId = event.transactionId
		log.info("Sending notification event | txId={}", txId)

		try {
			kafkaTemplate.send(KafkaTaskTopics.SEND_NOTIFICATION_TOPIC, txId, event)
			log.info("<< Notification event sent successfully | txId={}", txId)

		} catch (e: Exception) {
			log.error("Failed to send notification event | txId={}", txId, e)
			throw e
		}
	}
}
