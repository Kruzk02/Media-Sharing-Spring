package com.app.message.consumer;

import com.app.model.VerificationEmailEvent;
import com.app.service.VerificationSender;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class EmailEventConsumer {

  private final VerificationSender verificationSender;

  @KafkaListener(
      topics = "${kafka.topic.email-event.name}",
      groupId = "email-group",
      containerFactory = "emailKafkaListenerContainerFactory")
  public void listen(VerificationEmailEvent event) {
    log.info("Receive event from topic: {}", event.toString());
    try {
      verificationSender.sendVerification(event.userEmail(), event.verificationToken());
    } catch (Exception e) {
      log.error("Error while processing email event: {}", event, e);
    }
  }
}
