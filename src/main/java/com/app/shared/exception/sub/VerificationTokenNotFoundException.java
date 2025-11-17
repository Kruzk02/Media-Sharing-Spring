package com.app.shared.exception.sub;

public class VerificationTokenNotFoundException extends RuntimeException {
  public VerificationTokenNotFoundException(String message) {
    super(message);
  }
}
