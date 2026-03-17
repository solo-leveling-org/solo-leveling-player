package com.soloist.player.service.notification

import com.soloist.event.DomainEvent
import com.soloist.event.DomainEventType
import com.soloist.event.RoutingKeys
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class NotificationPublisher(
	private val rabbitTemplate: RabbitTemplate
) {

	private val log = LoggerFactory.getLogger(javaClass)

	fun sendTaskCompleted(userId: Long, taskTitle: String, gemReward: Int) {
		val event = DomainEvent(
			userId = userId,
			type = DomainEventType.TASK_COMPLETED,
			data = mapOf("taskTitle" to taskTitle, "gemReward" to gemReward)
		)
		log.debug("Publishing domain event: type={}, userId={}", event.type, userId)
		rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.domain(event.type), event)
	}

	fun sendDayStreakExtended(userId: Long) {
		val event = DomainEvent(
			userId = userId,
			type = DomainEventType.DAY_STREAK_EXTENDED
		)
		log.debug("Publishing domain event: type={}, userId={}", event.type, userId)
		rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.domain(event.type), event)
	}
}
