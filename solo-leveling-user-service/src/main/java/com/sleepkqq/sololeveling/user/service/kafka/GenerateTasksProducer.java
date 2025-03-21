package com.sleepkqq.sololeveling.user.service.kafka;

import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.GENERATE_TASKS_TOPIC;

import com.sleepkqq.sololeveling.avro.task.GenerateTasksEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTasksProducer {

  private final KafkaTemplate<String, GenerateTasksEvent> kafkaTemplate;

  public void send(GenerateTasksEvent event) {
    kafkaTemplate.send(GENERATE_TASKS_TOPIC, event);
    log.info("<< Generate tasks event sent | transactionId={}", event.getTransactionId());
  }
}
