package com.app.module.user.infrastructure.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
  public VerificationTokenNotFoundException(String message) {
    super(message);
  }
}
