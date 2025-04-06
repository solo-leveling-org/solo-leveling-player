package com.sleepkqq.sololeveling.player.service.kafka.producer;

import static com.sleepkqq.sololeveling.avro.constants.KafkaTaskTopics.SEND_NOTIFICATION_TOPIC;

import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendNotificationProducer {

  private final KafkaTemplate<String, SendNotificationEvent> kafkaTemplate;

  public void send(SendNotificationEvent event) {
    kafkaTemplate.send(SEND_NOTIFICATION_TOPIC, event);
    log.info(">> Notification sent | transactionId={}", event.getTransactionId());
  }
}
