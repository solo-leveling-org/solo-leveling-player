package com.sleepkqq.sololeveling.user.service.config;

import static com.sleepkqq.sololeveling.avro.constants.KafkaGroupIds.TASK_GROUP_ID;

import com.sleepkqq.sololeveling.avro.config.DefaultKafkaConfig;
import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent;
import com.sleepkqq.sololeveling.avro.task.SaveTasksEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration
public class KafkaConfig extends DefaultKafkaConfig {

  public KafkaConfig(
      @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
      @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistryUrl
  ) {
    super(bootstrapServers, schemaRegistryUrl);
  }

  @Bean
  public ProducerFactory<String, GenerateTasksEvent> producerFactoryGenerateTasksEvent() {
    return createProducerFactory();
  }

  @Bean
  public KafkaTemplate<String, GenerateTasksEvent> kafkaTemplateGenerateTasksEvent(
      ProducerFactory<String, GenerateTasksEvent> producerFactory
  ) {
    return createKafkaTemplate(producerFactory);
  }

  @Bean
  public ConsumerFactory<String, SaveTasksEvent> consumerFactorySaveTasksEvent() {
    return createConsumerFactory(TASK_GROUP_ID);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, SaveTasksEvent> kafkaListenerContainerFactorySaveTasksEvent(
      ConsumerFactory<String, SaveTasksEvent> consumerFactory
  ) {
    return createKafkaListenerContainerFactory(consumerFactory);
  }
}
