package com.sleepkqq.sololeveling.player.config

import com.sleepkqq.sololeveling.avro.config.DefaultKafkaConfig
import com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds
import com.sleepkqq.sololeveling.avro.idempotency.IdempotencyService
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Suppress("unused")
@EnableKafka
@Configuration
class KafkaConfig(
	@Value($$"${spring.kafka.bootstrap-servers}") bootstrapServers: String,
	@Value($$"${spring.kafka.properties.schema.registry.url}") schemaRegistryUrl: String
) : DefaultKafkaConfig(bootstrapServers, schemaRegistryUrl) {

	@Bean
	fun idempotencyService(
		template: StringRedisTemplate,
		@Value($$"${spring.application.name}") key: String
	): IdempotencyService = IdempotencyService(template, key)

	@Bean
	fun producerFactoryGenerateTasksEvent(): ProducerFactory<String, GenerateTasksEvent> =
		createProducerFactory()

	@Bean
	fun kafkaTemplateGenerateTasksEvent(
		producerFactory: ProducerFactory<String, GenerateTasksEvent>
	): KafkaTemplate<String, GenerateTasksEvent> = createKafkaTemplate(producerFactory)

	@Bean
	fun consumerFactorySaveTasksEvent(): ConsumerFactory<String, SaveTasksEvent> =
		createConsumerFactory(KafkaGroupIds.PLAYER_GROUP_ID)

	@Bean
	fun kafkaListenerContainerFactorySaveTasksEvent(
		consumerFactory: ConsumerFactory<String, SaveTasksEvent>
	): ConcurrentKafkaListenerContainerFactory<String, SaveTasksEvent> =
		createKafkaListenerContainerFactory(consumerFactory)

	@Bean
	fun producerFactorySaveTasksEvent(): ProducerFactory<String, SaveTasksEvent> =
		createProducerFactory()

	@Bean
	fun kafkaTemplateSaveTasksEvent(
		producerFactory: ProducerFactory<String, SaveTasksEvent>
	): KafkaTemplate<String, SaveTasksEvent> = createKafkaTemplate(producerFactory)

	@Bean
	fun producerFactorySendNotificationEvent(): ProducerFactory<String, SendNotificationEvent> =
		createProducerFactory()

	@Bean
	fun kafkaTemplateSendNotificationEvent(
		producerFactory: ProducerFactory<String, SendNotificationEvent>
	): KafkaTemplate<String, SendNotificationEvent> = createKafkaTemplate(producerFactory)
}
