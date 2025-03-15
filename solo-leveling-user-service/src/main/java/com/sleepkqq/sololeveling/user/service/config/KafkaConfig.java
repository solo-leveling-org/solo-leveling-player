package com.sleepkqq.sololeveling.user.service.config;

import com.sleepkqq.sololeveling.avro.config.DefaultKafkaConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class KafkaConfig extends DefaultKafkaConfig {

  public KafkaConfig(
      @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
      @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistryUrl
  ) {
    super(bootstrapServers, schemaRegistryUrl);
  }
}
