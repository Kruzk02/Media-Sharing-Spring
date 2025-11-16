package com.app.service;

public interface VerificationSender {
  void sendVerification(String to, String token);
}
