package com.soloist.player.config

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {

	@Bean
	fun jsonMessageConverter(): JacksonJsonMessageConverter =
		JacksonJsonMessageConverter()

	@Bean
	fun rabbitTemplate(connectionFactory: ConnectionFactory, converter: JacksonJsonMessageConverter): RabbitTemplate =
		RabbitTemplate(connectionFactory).apply { messageConverter = converter }
}
