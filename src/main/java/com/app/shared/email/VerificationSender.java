package com.app.shared.email;

public interface VerificationSender {
  void sendVerification(String to, String token);
}
