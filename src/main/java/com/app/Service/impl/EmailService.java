package com.app.Service.impl;

import com.app.Service.VerificationSender;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService implements VerificationSender {

  private final JavaMailSender mailSender;

  @Override
  public void sendVerification(String to, String code) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(to);
    mailMessage.setSubject("Verify your account");
    mailMessage.setText("Code: " + code);
    mailSender.send(mailMessage);
  }
}
