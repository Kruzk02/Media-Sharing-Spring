package com.app.module.email;

import com.app.shared.event.VerificationEmailEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class EmailEventListener {

  private final VerificationSender verificationSender;

  @ApplicationModuleListener
  public void handle(VerificationEmailEvent emailEvent) {
    log.info("Receive event from topic: {}", emailEvent.toString());
    try {
      verificationSender.sendVerification(emailEvent.userEmail(), emailEvent.verificationToken());
    } catch (Exception e) {
      log.error("Error while processing email event: {}", emailEvent, e);
    }
  }
}
