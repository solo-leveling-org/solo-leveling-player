package com.sleepkqq.sololeveling.user.service.kafka;

import com.sleepkqq.sololeveling.avro.task.GetNewTasksEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateTasksProducer {

  private static final String TOPIC = "get-tasks";

  private final KafkaTemplate<String, GetNewTasksEvent> kafkaTemplate;

  public void getNewTasks(GetNewTasksEvent event) {
    kafkaTemplate.send(TOPIC, event);
    log.info("<< Get new tasks event sent | transactionId={}", event.getTransactionId());
  }
}
