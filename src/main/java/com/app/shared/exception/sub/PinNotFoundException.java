package com.app.shared.exception.sub;

public class PinNotFoundException extends RuntimeException {
  public PinNotFoundException(String message) {
    super(message);
  }
}
