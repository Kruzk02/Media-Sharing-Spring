package com.app.module.user.domain.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
  public VerificationTokenNotFoundException(String message) {
    super(message);
  }
}
