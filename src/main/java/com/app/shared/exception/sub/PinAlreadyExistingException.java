package com.app.shared.exception.sub;

public class PinAlreadyExistingException extends RuntimeException {
  public PinAlreadyExistingException(String message) {
    super(message);
  }
}
