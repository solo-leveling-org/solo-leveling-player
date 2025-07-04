package com.sleepkqq.sololeveling.player.service.config

import com.sleepkqq.sololeveling.avro.config.DefaultKafkaConfig
import com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@EnableKafka
@Configuration
class KafkaConfig(
	@Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
	@Value("\${spring.kafka.properties.schema.registry.url}") schemaRegistryUrl: String
) : DefaultKafkaConfig(bootstrapServers, schemaRegistryUrl) {

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
	fun producerFactorySendNotificationEvent(): ProducerFactory<String, SendNotificationEvent> =
		createProducerFactory()

	@Bean
	fun kafkaTemplateSendNotificationEvent(
		producerFactory: ProducerFactory<String, SendNotificationEvent>
	): KafkaTemplate<String, SendNotificationEvent> = createKafkaTemplate(producerFactory)
}
