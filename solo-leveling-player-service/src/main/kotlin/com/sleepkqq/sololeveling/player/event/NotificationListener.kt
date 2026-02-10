package com.sleepkqq.sololeveling.player.event

import com.sleepkqq.sololeveling.avro.notification.Notification
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.player.event.model.NotificationEvent
import com.sleepkqq.sololeveling.player.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.service.i18n.I18nService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class NotificationListener(
	private val i18nService: I18nService,
	private val sendNotificationProducer: SendNotificationProducer
) {

	private val log = LoggerFactory.getLogger(javaClass)

	@EventListener
	fun listen(event: NotificationEvent) {
		try {
			val ctx = event.toContext()
			val message = ctx.localizationCode?.let { i18nService.getMessage(it) }
			val notification = Notification(message, ctx.type, ctx.source, ctx.visible)
			val sendEvent = SendNotificationEvent(ctx.txId, ctx.userId, ctx.priority, notification)

			sendNotificationProducer.send(sendEvent)

		} catch (e: Exception) {
			log.error("Failed to send notification for user {}", event.userId, e)
		}
	}
}
