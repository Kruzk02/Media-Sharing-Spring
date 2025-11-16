package com.app.service.impl;

import com.app.service.VerificationSender;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService implements VerificationSender {

  private final JavaMailSender mailSender;

  @Override
  public void sendVerification(String to, String token) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(to);
    mailMessage.setSubject("Verify your account");
    mailMessage.setText("Token: " + token);
    mailSender.send(mailMessage);
  }
}
