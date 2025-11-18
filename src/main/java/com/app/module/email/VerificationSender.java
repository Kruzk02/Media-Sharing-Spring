package com.app.module.email;

public interface VerificationSender {
  void sendVerification(String to, String token);
}
